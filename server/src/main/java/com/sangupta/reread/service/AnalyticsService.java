package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.UserActivity;

public interface AnalyticsService {
	
	public boolean recordNewPosts(List<Post> posts);

	public boolean recordUserActivity(UserActivity activity, Post post);
	
}
