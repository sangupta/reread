package com.sangupta.reread.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.DateUtils;
import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.service.FeedCrawlDetailsService;
import com.sangupta.reread.service.FeedCrawlerService;
import com.sangupta.reread.service.FeedParsingService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.MasterFeedService;
import com.sangupta.reread.service.PostService;
import com.sangupta.reread.service.PostSnippetService;

/**
 * {@link HttpService} based implementation of the {@link FeedCrawlerService}.
 * 
 * @author sangupta
 *
 */
@Service
public class HttpFeedCrawlerServiceImpl implements FeedCrawlerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFeedCrawlerServiceImpl.class);
	
	@Autowired
	protected MasterFeedService masterFeedService;
	
	@Autowired
	protected FeedCrawlDetailsService feedCrawlDetailsService;
	
	@Autowired
	protected FeedParsingService feedParsingService;
	
	@Autowired
	protected PostSnippetService postSnippetService;
	
	@Autowired
	protected FeedTimelineService feedTimelineService;
	
	@Autowired
	protected PostService postService;
	
	@Override
	public void crawlFeed(String masterFeedID, boolean forceUpdateAllTimeline) {
		MasterFeed masterFeed = this.masterFeedService.get(masterFeedID);
		if(masterFeed == null) {
			LOGGER.warn("Master feed not found for id: {}", masterFeed);
			return;
		}
		
		// create details object as needed
		FeedCrawlDetails details = this.feedCrawlDetailsService.get(masterFeedID);
		
		if(details != null) {
			long delta = System.currentTimeMillis() - details.lastCrawled;
			if(delta < DateUtils.ONE_MINUTE) {
				LOGGER.info("Feed was crawled within last one minute, skipping");
				return;
			}
		}

		// create a new one if needed
		if(details == null) {
			LOGGER.debug("Feed has not been crawled before, creating details object for id: {}", masterFeed);
			
			details = new FeedCrawlDetails();
			details.feedID = masterFeedID;
			this.feedCrawlDetailsService.upsert(details);
		}
		
		// read feed
		ParsedFeed parsedFeed = this.feedParsingService.parseFeedFromUrl(masterFeed.feedID, masterFeed.url, details.latestPostID);
		
		// update last crawl time
		this.feedCrawlDetailsService.updateField(details, "lastCrawled", System.currentTimeMillis());
		
		// if empty, we are done
		if(parsedFeed == null) {
			LOGGER.warn("Parsing did not yield any posts for id: {}", masterFeed);
			return;
		}

		// update master feed
		final List<Post> posts = parsedFeed.posts;

		this.updateEntities(masterFeed, details, parsedFeed, posts);
		
		// create snippets
		for(Post post : posts) {
			this.postSnippetService.createSnippet(parsedFeed.siteUrl, post);
		}
		
		// de-dupe entries
		this.postService.filterAlreadyExistingPosts(posts);
		
		// create snippets and store each entry
		if(AssertUtils.isEmpty(posts)) {
			// nothing to save
			LOGGER.debug("No posts available to save after filtering for feed: {}", masterFeed);
			return;
		}
		
		// store each post in DB
		this.postService.savePosts(posts);
		
		// update the feed timeline
		this.feedTimelineService.addToTimeline(masterFeedID, posts, forceUpdateAllTimeline);
	}

	protected void updateEntities(MasterFeed feed, FeedCrawlDetails details, ParsedFeed parsedFeed, List<Post> posts) {
		this.masterFeedService.updateField(feed, "title", parsedFeed.feedTitle);
		this.masterFeedService.updateField(feed, "siteUrl", parsedFeed.siteUrl);

		if(AssertUtils.isNotEmpty(posts)) {
			details.latestPostID = posts.get(0).uniqueID;
			if(AssertUtils.isEmpty(details.latestPostID)) {
				details.latestPostID = "hash:" + posts.get(0).hash;
			}
		}
		
		details.lastCrawled = parsedFeed.crawlTime;
		details.lastModifiedHeader = parsedFeed.lastModifiedHeader;
		details.lastModifiedTime = parsedFeed.lastModifiedTimestamp;
		details.numCrawled++;
		details.etag = parsedFeed.eTagHeader;

		this.feedCrawlDetailsService.update(details);
	}

}
