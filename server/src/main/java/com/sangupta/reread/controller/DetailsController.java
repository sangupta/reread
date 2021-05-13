package com.sangupta.reread.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.service.FeedCrawlDetailsService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.MasterFeedService;

/**
 * REST controller which serves details to client about a single feed.
 * 
 * @author sangupta
 *
 */
@RestController
@RequestMapping("/details")
public class DetailsController {

	@Autowired
	protected FeedCrawlDetailsService feedCrawlDetailsService;

	@Autowired
	protected MasterFeedService masterFeedService;

	@Autowired
	protected FeedTimelineService feedTimelineService;

	@GetMapping("/feed/{feedID}")
	public CrawlDetailsPayload getFeedDetails(@PathVariable String feedID) {
		MasterFeed mf = this.masterFeedService.get(feedID);
		FeedCrawlDetails crawlDetails = this.feedCrawlDetailsService.get(feedID);
		String latestID = this.feedTimelineService.getLatestID(feedID);
		long numPosts = this.feedTimelineService.size(feedID);

		CrawlDetailsPayload payload = new CrawlDetailsPayload();

		payload.title = mf.title;
		payload.feedUrl = mf.url;
		payload.siteUrl = mf.siteUrl;
		payload.iconUrl = mf.iconUrl;
		payload.subscribed = mf.added;

		payload.lastCrawled = crawlDetails.lastCrawled;
		payload.totalPosts = numPosts;

		return payload;
	}

	public static class CrawlDetailsPayload {
		public String title;
		public String siteUrl;
		public String feedUrl;
		public String iconUrl;
		public long subscribed;
		public long lastPost;
		public long lastCrawled;
		public long totalPosts;
	}
}
