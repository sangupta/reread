package com.sangupta.reread.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.service.FeedCrawlDetailsService;
import com.sangupta.reread.service.FeedCrawlerService;
import com.sangupta.reread.service.FeedParsingService;
import com.sangupta.reread.service.MasterFeedService;

public class HttpFeedCrawlerServiceImpl implements FeedCrawlerService {
	
	@Autowired
	protected MasterFeedService masterFeedService;
	
	@Autowired
	protected FeedCrawlDetailsService feedCrawlDetailsService;
	
	@Autowired
	protected FeedParsingService feedReaderService;

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
		
		// create snippets and store each entry
		
		// update the timeline
		
	}

	private void updateEntities(MasterFeed feed, FeedCrawlDetails details, ParsedFeed parsedFeed) {
		this.masterFeedService.updateField(feed, "title", parsedFeed.feedTitle);
		this.masterFeedService.updateField(feed, "siteUrl", parsedFeed.siteUrl);

		details.lastCrawled = parsedFeed.crawlTime;
		details.lastModifiedHeader = parsedFeed.lastModifiedHeader;
		details.numCrawled++;
		details.etag = parsedFeed.eTagHeader;

		this.feedCrawlDetailsService.update(details);
	}

}
