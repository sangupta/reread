package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.AnalyticsService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.PostSearchService;
import com.sangupta.reread.service.PostService;

@Service
public class RedisPostServiceImpl extends RedisDataStoreServiceImpl<Post> implements PostService {

	@Autowired
	protected PostSearchService postSearchService;

	@Autowired
	protected RedisTemplate<String, String> redisTemplate;

	@Autowired
	protected FeedTimelineService feedTimelineService;
	
	@Autowired
	protected AnalyticsService analyticsService;

	@Override
	public void filterAlreadyExistingPosts(List<Post> posts) {
		if (AssertUtils.isEmpty(posts)) {
			return;
		}

		// this is to filter posts across all feeds and
		// not just the same feed
	}

	@Override
	public void savePosts(List<Post> posts) {
		if (AssertUtils.isEmpty(posts)) {
			return;
		}

		for (Post post : posts) {
			this.insert(post);
			
			// also index this post for search
			this.postSearchService.indexPost(post);
		}

		// track via analytics
		this.analyticsService.recordNewPosts(posts);
	}

	@Override
	public Post markRead(String postID) {
		return this.markPost(postID, "readOn", FeedTimelineService.READ_TIMELINE_ID);
	}

	@Override
	public Post markUnread(String postID) {
		return this.unmarkPost(postID, "readOn", FeedTimelineService.READ_TIMELINE_ID);
	}

	@Override
	public Post starPost(String postID) {
		return this.markPost(postID, "starredOn", FeedTimelineService.STARRED_TIMELINE_ID);
	}

	@Override
	public Post unstarPost(String postID) {
		return this.unmarkPost(postID, "starredOn", FeedTimelineService.STARRED_TIMELINE_ID);
	}

	@Override
	public Post bookmarkPost(String postID) {
		return this.markPost(postID, "bookmarkedOn", FeedTimelineService.BOOKMARK_TIMELINE_ID);
	}

	@Override
	public Post unbookmarkPost(String postID) {
		return this.unmarkPost(postID, "bookmarkedOn", FeedTimelineService.BOOKMARK_TIMELINE_ID);
	}

	private Post markPost(String postID, String fieldName, String timelineID) {
		Post post = this.get(postID);
		if (post != null) {
			long time = System.currentTimeMillis();
			this.updateField(post, fieldName, time);
			this.feedTimelineService.addToSpecialTimeline(timelineID, postID, 0);
			
			post = this.get(postID);
		}
		
		return post;
	}

	private Post unmarkPost(String postID, String fieldName, String timelineID) {
		Post post = this.get(postID);
		if (post != null) {
			this.updateField(post, fieldName, 0l);
			this.feedTimelineService.removeFromSpecialTimeline(timelineID, postID);
			
			post = this.get(postID);
		}
		
		return post;
	}

}
