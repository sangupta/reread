package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.service.FeedCrawlerService;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.FeedSubscriptionService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.MasterFeedService;

/**
 * Default implementation to the {@link FeedSubscriptionService}.
 * 
 * @author sangupta
 *
 */
@Service
public class DefaultFeedSubscriptionServiceImpl implements FeedSubscriptionService {
	
	@Autowired
	protected MasterFeedService masterFeedService;
	
	@Autowired
	protected FeedListService feedListService;
	
	@Autowired
	protected FeedCrawlerService feedCrawlerService;

	@Autowired
	protected FeedTimelineService feedTimelineService;
	
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
		if(!feedList.containsFeed(feedID)) {
			return feedList;
		}
		
		// remove feed from list
		feedList.removeFeed(feedID);
		this.feedListService.update(feedList);
		
		// get all feed posts
		List<String> ids = this.feedTimelineService.getTimeLine(feedID);
		if(AssertUtils.isNotEmpty(ids)) {
			for(String id : ids) {
				this.feedTimelineService.removeFromSpecialTimeline(FeedTimelineService.ALL_TIMELINE_ID, id);
				this.feedTimelineService.removePost(FeedTimelineService.ALL_TIMELINE_ID, id);
			}
		}
		
		// all done
		return feedList;
	}

}
