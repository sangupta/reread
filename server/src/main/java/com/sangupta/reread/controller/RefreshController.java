package com.sangupta.reread.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.DateUtils;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.UserFeed;
import com.sangupta.reread.entity.UserFeedFolder;
import com.sangupta.reread.service.FeedCrawlerService;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.FeedRefreshService;
import com.sangupta.reread.web.SingleMeUserFilter;

/**
 * REST end points around refreshing feeds.
 * 
 * @author sangupta
 *
 */
@RestController
@RequestMapping("/refresh")
public class RefreshController {
	
	@Autowired
	protected FeedCrawlerService feedCrawlerService;

	@Autowired
	protected FeedListService feedListService;
	
	@Autowired
	protected FeedRefreshService feedRefreshService;
	
	/**
	 * Refresh a given feed sync.
	 * 
	 * @param feedID
	 * @return
	 */
	@GetMapping("/feed/{feedID}")
	public String refreshFeed(@PathVariable String feedID) {
		this.feedCrawlerService.crawlFeed(feedID);
		return "done";
	}
	
	/**
	 * Refresh a given folder async.
	 * 
	 * @param folderID
	 * @return
	 */
	@GetMapping("/folder/{folderID}")
	public String refreshFolder(@PathVariable String folderID) {
		FeedList list = this.feedListService.get(SecurityContext.getUserID());
		UserFeedFolder folder = list.getFolder(folderID);
		
		for(UserFeed feed : folder.childFeeds) {
			this.feedRefreshService.refreshFeed(feed.masterFeedID);
		}
		
		return "done";
	}
	
	/**
	 * Refresh all feeds in the feed list async.
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public String refreshAll() {
		FeedList list = this.feedListService.get(SecurityContext.getUserID());
		if(list == null) {
			return "nothing to do";
		}

		// folders
		for(UserFeedFolder folder : list.folders) {
			for(UserFeed feed : folder.childFeeds) {
				this.feedRefreshService.refreshFeed(feed.masterFeedID);
			}
		}
		
		// individual feeds
		for(UserFeed feed : list.feeds) {
			this.feedRefreshService.refreshFeed(feed.masterFeedID);
		}
		
		return "done";
	}

	/**
	 * Refresh all feeds in user's feed list async.
	 * 
	 */
	@Scheduled(fixedDelay = DateUtils.FIVE_MINUTES)
	public void backgroundRefresh() {
		SecurityContext.setPrincipal(SingleMeUserFilter.USER_ME);
		this.refreshAll();
	}
	
}
