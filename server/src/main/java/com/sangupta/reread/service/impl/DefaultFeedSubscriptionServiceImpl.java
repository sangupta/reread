package com.sangupta.reread.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.service.FeedCrawlerService;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.FeedSubscriptionService;
import com.sangupta.reread.service.MasterFeedService;

@Service
public class DefaultFeedSubscriptionServiceImpl implements FeedSubscriptionService {
	
	@Autowired
	protected MasterFeedService masterFeedService;
	
	@Autowired
	protected FeedListService feedListService;
	
	@Autowired
	protected FeedCrawlerService feedCrawlerService;

	@Override
	public MasterFeed subscribe(MasterFeed mf) {
		// find master feed for one
		MasterFeed feed = this.masterFeedService.getOrCreateFeed(mf);
		
		// add the same to the user's subscribe list
		FeedList feedList = this.feedListService.getOrCreate(SecurityContext.getUserID());
		if(feedList.containsFeed(feed.feedID)) {
			throw new HttpException(HttpStatusCode.CONFLICT, "Feed already subscribed");
		}
		
		// crawl this feed now
		this.feedCrawlerService.crawlFeed(feed.feedID);
		
		// re-read master feed as title may have changed
		feed = this.masterFeedService.get(feed.feedID);
		
		// add feed to list
		feedList.addFeed(feed);
		this.feedListService.update(feedList);
		
		// all done
		return feed;
	}

	@Override
	public FeedList unsubscribe(String feedID) {
		FeedList feedList = this.feedListService.getOrCreate(SecurityContext.getUserID());
		if(feedList.containsFeed(feedID)) {
			return feedList;
		}
		
		// remove feed from list
		feedList.removeFeed(feedID);
		this.feedListService.update(feedList);
		
		// all done
		return feedList;
	}

}
