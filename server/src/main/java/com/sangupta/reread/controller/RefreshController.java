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

@RestController
@RequestMapping("/refresh")
public class RefreshController {
	
	@Autowired
	protected FeedCrawlerService feedCrawlerService;

	@Autowired
	protected FeedListService feedListService;
	
	@Autowired
	protected FeedRefreshService feedRefreshService;
	
	@GetMapping("/feed/{feedID}")
	public String refreshFeed(@PathVariable String feedID) {
		this.feedCrawlerService.crawlFeed(feedID);
		return "done";
	}
	
	@GetMapping("/folder/{folderID}")
	public String refreshFolder(@PathVariable String folderID) {
		FeedList list = this.feedListService.get(SecurityContext.getUserID());
		UserFeedFolder folder = list.getFolder(folderID);
		
		for(UserFeed feed : folder.childFeeds) {
			this.feedRefreshService.refreshFeed(feed.masterFeedID);
		}
		
		return "done";
	}
	
	@GetMapping("/all")
	public String refreshAll() {
		FeedList list = this.feedListService.get(SecurityContext.getUserID());

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
	
	@Scheduled(fixedDelay = DateUtils.FIVE_MINUTES)
	public void backgroundRefresh() {
		SecurityContext.setPrincipal(SingleMeUserFilter.USER_ME);
		this.refreshAll();
	}
	
}
