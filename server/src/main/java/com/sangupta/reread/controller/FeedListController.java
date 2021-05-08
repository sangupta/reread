package com.sangupta.reread.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.reread.entity.Feed;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.service.FeedDiscoveryService;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.FeedSubscriptionService;

@RestController
@RequestMapping("/feeds")
public class FeedListController {

	@Autowired
	protected FeedListService feedListService;
	
	@Autowired
	protected FeedSubscriptionService feedSubscriptionService;
	
	@Autowired
	protected FeedDiscoveryService feedDiscoveryService;

	@GetMapping("/me")
	public FeedList getFeedList() {
		FeedList list = this.feedListService.get(SecurityContext.getUserID());
		if(list == null) {
			list = new FeedList();
			list.userID = SecurityContext.getUserID();
		}
		
		return list;
	}
	
	@PostMapping("/subscribe")
	public FeedList subscribe(@RequestBody FeedControllerPayload payload) {
		boolean susbcribed = this.feedSubscriptionService.subscribe(payload.url);
		return this.getFeedList();
	}
	
	@PostMapping("/unsubscribe")
	public FeedList unsubscribe(@RequestBody FeedControllerPayload payload) {
		boolean unsusbcribed = this.feedSubscriptionService.unsubscribe(payload.feedID);
		return this.getFeedList();
	}
	
	@PostMapping("/discover")
	public Set<Feed> discoverFeed(@RequestBody FeedControllerPayload payload) {
		return this.feedDiscoveryService.discoverFeeds(payload.url);
	}

	private static class FeedControllerPayload {
		String url;
		String feedID;
	}
}
