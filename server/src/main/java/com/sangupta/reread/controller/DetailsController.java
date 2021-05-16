package com.sangupta.reread.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.util.DateUtils;
import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.UserActivity;
import com.sangupta.reread.service.AnalyticsService;
import com.sangupta.reread.service.FeedCrawlDetailsService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.MasterFeedService;
import com.sangupta.reread.service.PostService;

/**
 * REST controller which serves details to client about a single feed.
 * 
 * @author sangupta
 *
 */
@RestController
@RequestMapping("/details")
public class DetailsController {
	
	public static final String REREAD_SERVER_START = "$reread-start-time";

	@Autowired
	protected FeedCrawlDetailsService feedCrawlDetailsService;

	@Autowired
	protected MasterFeedService masterFeedService;

	@Autowired
	protected FeedTimelineService feedTimelineService;
	
	@Autowired
	protected PostService postService;

	@Autowired
	protected AnalyticsService analyticsService;
	
	@Autowired
	protected RedisTemplate<String, String> redisTemplate;
	
	@PostConstruct
	public void init() {
		this.redisTemplate.opsForValue().setIfAbsent(REREAD_SERVER_START, String.valueOf(System.currentTimeMillis()));
	}

	@GetMapping("/feed/{feedID}")
	public CrawlDetailsPayload getFeedDetails(@PathVariable String feedID) {
		MasterFeed mf = this.masterFeedService.get(feedID);
		FeedCrawlDetails crawlDetails = this.feedCrawlDetailsService.get(feedID);
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
		String id = this.feedTimelineService.getOldestPostID(feedID);
		Post post = this.postService.get(id);
		
		long duration = Long.parseLong(interval) * 60l * 1000l;
		return this.analyticsService.getFeedChart(feedID, post.updated, System.currentTimeMillis(), duration, metrics);
	}
	
	@GetMapping("/chart/activity/{activity}")
	public Object getActivityData(@PathVariable UserActivity activity, @RequestParam(required = false, defaultValue = "1") String interval, @RequestParam(required = false) String metrics) {
		Long startTime = Long.parseLong(this.redisTemplate.opsForValue().get(REREAD_SERVER_START));
		
		long start = startTime != null ? (startTime.longValue() - DateUtils.ONE_DAY) : 0;
		long end = System.currentTimeMillis() + DateUtils.ONE_DAY;
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
