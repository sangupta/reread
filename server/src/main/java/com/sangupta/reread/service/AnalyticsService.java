package com.sangupta.reread.service;

import java.util.List;

import com.redislabs.redistimeseries.Value;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.UserActivity;

/**
 * Track user activity behavior and feed posting behavior.
 * 
 * @author sangupta
 *
 */
public interface AnalyticsService {
	
	/**
	 * Record new posts as they are published. This allows us to 
	 * see how frequently a feed is publishing.
	 * 
	 * @param posts
	 * @return
	 */
	public boolean recordNewPosts(List<Post> posts);

	/**
	 * Record user activity around reading, starring and bookmarking
	 * posts. This allows the user to see how they are using the system.
	 * 
	 * @param activity
	 * @param post
	 * @return
	 */
	public boolean recordUserActivity(UserActivity activity, Post post);
	
	public Value[] getFeedChart(String feedID, long start, long end, long interval, String metrics);
	
	public Value[] getActivityChart(UserActivity activity, long start, long end, long interval, String metrics);
	
}
