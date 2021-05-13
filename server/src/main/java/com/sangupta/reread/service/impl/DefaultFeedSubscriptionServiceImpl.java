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
import com.sangupta.reread.entity.UserFeedFolder;
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
	public MasterFeed subscribe(MasterFeed mf, String folder) {
		// find master feed for one
		MasterFeed masterFeed = this.masterFeedService.getOrCreateFeed(mf);
		
		// add the same to the user's subscribe list
		FeedList feedList = this.feedListService.getOrCreate(SecurityContext.getUserID());
		if(feedList.containsFeed(masterFeed.feedID)) {
			throw new HttpException(HttpStatusCode.CONFLICT, "Feed already subscribed");
		}
		
		// crawl this feed now
		this.feedCrawlerService.crawlFeed(masterFeed.feedID);
		
		// re-read master feed as title may have changed
		masterFeed = this.masterFeedService.get(masterFeed.feedID);
		
		// add feed to list
		if(AssertUtils.isEmpty(folder)) {
			feedList.addFeed(masterFeed);
		} else {
			UserFeedFolder feedFolder = feedList.getFolder(folder);
			feedFolder.addFeed(masterFeed);
		}
		
		this.feedListService.update(feedList);
		
		// all done
		return masterFeed;
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
