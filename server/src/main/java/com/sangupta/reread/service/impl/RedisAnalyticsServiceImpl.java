package com.sangupta.reread.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.redislabs.redistimeseries.RedisTimeSeries;
import com.sangupta.reread.SpringBeans;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.UserActivity;
import com.sangupta.reread.service.AnalyticsService;

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
		
		for(Post post : posts) {
			String key = "timeSeries-feed:" + post.masterFeedID;
			this.createTimeSeriesIfNeeded(key);
			
			if(post.feedPostID == null) {
				System.out.println("break");
			}
			
			long timestamp = post.updated;
			Map<String, String> labels = new HashMap<>(); 
			labels.put("postID", post.feedPostID); 
		
			this.timeSeries.add(key, timestamp, 1, labels);
		}
		
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
	
}
