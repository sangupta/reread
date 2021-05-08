package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangupta.jerry.util.AssertUtils;
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

public class HttpFeedCrawlerServiceImpl implements FeedCrawlerService {
	
	@Autowired
	protected MasterFeedService masterFeedService;
	
	@Autowired
	protected FeedCrawlDetailsService feedCrawlDetailsService;
	
	@Autowired
	protected FeedParsingService feedReaderService;
	
	@Autowired
	protected PostSnippetService postSnippetService;
	
	@Autowired
	protected FeedTimelineService feedTimelineService;
	
	@Autowired
	protected PostService postService;

	@Override
	public void crawlFeed(String masterFeedID) {
		MasterFeed masterFeed = this.masterFeedService.get(masterFeedID);
		if(masterFeed == null) {
			return;
		}
		
		// create details object as needed
		FeedCrawlDetails details = this.feedCrawlDetailsService.get(masterFeedID);
		if(details == null) {
			details = new FeedCrawlDetails();
			details.feedID = masterFeedID;
			this.feedCrawlDetailsService.insert(details);
		}
		
		// read feed
		ParsedFeed parsedFeed = this.feedReaderService.parseFeedFromUrl(masterFeed.url);
		
		// update last crawl time
		this.feedCrawlDetailsService.updateField(details, "lastCrawled", System.currentTimeMillis());
		
		// if empty, we are done
		if(parsedFeed == null) {
			return;
		}

		// update master feed
		this.updateEntities(masterFeed, details, parsedFeed);
		
		final List<Post> posts = parsedFeed.posts;
		
		// de-dupe entries
		this.postService.filterAlreadyExistingPosts(parsedFeed);
		
		// create snippets and store each entry
		if(AssertUtils.isEmpty(posts)) {
			// nothing to save
			return;
		}
		
		// create snippets
		for(Post post : posts) {
			this.postSnippetService.createSnippets(post);
		}
		
		// store each post in DB
		this.postService.savePosts(posts);
		
		// update the timeline
		this.feedTimelineService.updateTimeline(posts);
	}

	protected void updateEntities(MasterFeed feed, FeedCrawlDetails details, ParsedFeed parsedFeed) {
		this.masterFeedService.updateField(feed, "title", parsedFeed.feedTitle);
		this.masterFeedService.updateField(feed, "siteUrl", parsedFeed.siteUrl);

		details.lastCrawled = parsedFeed.crawlTime;
		details.lastModifiedHeader = parsedFeed.lastModifiedHeader;
		details.numCrawled++;
		details.etag = parsedFeed.eTagHeader;

		this.feedCrawlDetailsService.update(details);
	}

}
