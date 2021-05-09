package com.sangupta.reread.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.reread.entity.DiscoveredFeed;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.MasterFeed;
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
		return this.feedListService.getOrCreate(SecurityContext.getUserID());
	}

	@PostMapping("/subscribe")
	public MasterFeed subscribe(@RequestBody FeedControllerPayload payload) {
		return this.feedSubscriptionService.subscribe(payload.url);
	}

	@PostMapping("/unsubscribe")
	public FeedList unsubscribe(@RequestBody FeedControllerPayload payload) {
		return this.feedSubscriptionService.unsubscribe(payload.feedID);
	}

	@PostMapping("/discover")
	public Set<DiscoveredFeed> discoverFeed(@RequestBody FeedControllerPayload payload) {
		return this.feedDiscoveryService.discoverFeeds(payload.url);
	}

	@SuppressWarnings("unused")
	private static class FeedControllerPayload {
		String url;
		String feedID;

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param url the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * @return the feedID
		 */
		public String getFeedID() {
			return feedID;
		}

		/**
		 * @param feedID the feedID to set
		 */
		public void setFeedID(String feedID) {
			this.feedID = feedID;
		}
	}
}
