package com.sangupta.reread.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.UserActivity;
import com.sangupta.reread.service.AnalyticsService;
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

	@Autowired
	protected AnalyticsService analyticsService;

	@GetMapping("/feed/{feedID}")
	public CrawlDetailsPayload getFeedDetails(@PathVariable String feedID) {
		MasterFeed mf = this.masterFeedService.get(feedID);
		FeedCrawlDetails crawlDetails = this.feedCrawlDetailsService.get(feedID);
		String latestID = this.feedTimelineService.getLatestID(feedID);
		long numPosts = this.feedTimelineService.size(feedID);

		CrawlDetailsPayload payload = new CrawlDetailsPayload();

		payload.feedID = feedID;
		payload.title = mf.title;
		payload.feedUrl = mf.url;
		payload.siteUrl = mf.siteUrl;
		payload.iconUrl = mf.iconUrl;
		payload.subscribed = mf.added;

		payload.lastCrawled = crawlDetails.lastCrawled;
		payload.totalPosts = numPosts;

		return payload;
	}
	
	@GetMapping("/chart/feed/{feedID}")
	public Object getFeedData(@PathVariable String feedID, @RequestParam(required = false, defaultValue = "1") String interval, @RequestParam(required = false) String metrics) {
		MasterFeed mf = this.masterFeedService.get(feedID);
		if(mf == null) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}
		
		long duration = Long.parseLong(interval) * 60l * 1000l;
		return this.analyticsService.getFeedChart(feedID, mf.added, System.currentTimeMillis(), duration, metrics);
	}
	
	@GetMapping("/chart/activity/{activity}")
	public Object getActivityData(@PathVariable UserActivity activity, @RequestParam(required = false) long start, @RequestParam(required = false) long end, @RequestParam(required = false) String interval, @RequestParam(required = false) String metrics) {
		long duration = Long.parseLong(interval) * 60l * 1000l;
		return this.analyticsService.getActivityChart(activity, start, end, duration, metrics);
	}

	public static class CrawlDetailsPayload {
		public String feedID;
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
