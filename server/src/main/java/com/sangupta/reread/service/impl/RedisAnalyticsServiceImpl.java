package com.sangupta.reread.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.redislabs.redistimeseries.Aggregation;
import com.redislabs.redistimeseries.Measurement;
import com.redislabs.redistimeseries.RedisTimeSeries;
import com.redislabs.redistimeseries.Value;
import com.sangupta.reread.SpringBeans;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.UserActivity;
import com.sangupta.reread.service.AnalyticsService;

/**
 * {@link RedisTimeSeries} based implementation of the {@link AnalyticsService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisAnalyticsServiceImpl implements AnalyticsService {
	
	protected RedisTimeSeries timeSeries;
	
	@Autowired
	protected RedisTemplate<String, String> redisTemplate;
	
	public RedisAnalyticsServiceImpl() {
		this.timeSeries = new RedisTimeSeries(SpringBeans.REDIS_HOST, SpringBeans.REDIS_PORT);		
	}
	
	@Override
	public boolean recordNewPosts(List<Post> posts) {
		
		Measurement[] measurements = new Measurement[posts.size()];
		int index = 0; 
		for(Post post : posts) {
			String key = "timeSeries-feed:" + post.masterFeedID;
			this.createTimeSeriesIfNeeded(key);
			
			long timestamp = post.updated;
			Map<String, String> labels = new HashMap<>(); 
			labels.put("postID", post.feedPostID); 
		
			measurements[index]= new Measurement(key, timestamp, 1);
			index++;
		}
		
		this.timeSeries.madd(measurements);
		return true;
	}

	@Override
	public boolean recordUserActivity(UserActivity activity, Post post) {
		String key = "timeSeries-activity:" + activity;
		this.createTimeSeriesIfNeeded(key);
		
		long timestamp = System.currentTimeMillis();
		Map<String, String> labels = new HashMap<>(); 
		labels.put("postID", post.feedPostID); 

		this.timeSeries.add(key, timestamp, 1, labels);
		return true;
	}

	protected void createTimeSeriesIfNeeded(String key) {
		Boolean exists = this.redisTemplate.hasKey(key);
		if(exists != null && !exists) {
			this.timeSeries.create(key);
		}		
	}
	
	@Override
	public Value[] getFeedChart(String feedID, long start, long end, long interval, String metrics) {
		String key = "timeSeries-feed:" + feedID;
		Aggregation aggregation = this.getAggregation(metrics);

		return this.timeSeries.range(key, start, end, aggregation, interval);
	}
	
	@Override
	public Value[] getActivityChart(UserActivity activity, long start, long end, long interval, String metrics) {
		String key = "timeSeries-activity:" + activity;
		boolean exists = this.redisTemplate.hasKey(key);
		if(!exists) {
			return null;
		}
		
		Aggregation aggregation = this.getAggregation(metrics);

		return this.timeSeries.range(key, start, end, aggregation, interval);
	}

	protected Aggregation getAggregation(String metrics) {
		if ("avg".equalsIgnoreCase(metrics)) {
			return Aggregation.AVG;
		}
		if ("count".equalsIgnoreCase(metrics)) {
			return Aggregation.COUNT;
		}
		if ("min".equalsIgnoreCase(metrics)) {
			return Aggregation.MIN;
		}
		if ("max".equalsIgnoreCase(metrics)) {
			return Aggregation.MAX;
		}
		
		return Aggregation.COUNT;
	}
	
}
