package com.sangupta.reread.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.redislabs.modules.rejson.JReJSON;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.SpringBeans;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.AnalyticsService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.PostSearchService;
import com.sangupta.reread.service.PostService;

import io.rebloom.client.Client;

/**
 * Redis {@link JReJSON} based implementation of the {@link PostService}.
 * 
 * @author sangupta
 *
 */
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
	
	protected Client bloomFilter = new Client(SpringBeans.REDIS_HOST, SpringBeans.REDIS_PORT);

	@Override
	public void filterAlreadyExistingPosts(List<Post> posts) {
		if (AssertUtils.isEmpty(posts)) {
			return;
		}

		// this is to filter posts across all feeds and
		// not just the same feed
		Iterator<Post> postIterator = posts.iterator();
		while(postIterator.hasNext()) {
			Post post = postIterator.next();
			boolean exists = this.bloomFilter.exists("bloom:hash", post.hash);
			if(!exists) {
				continue;
			}
			
			// pure text
			exists = this.bloomFilter.exists("bloom:text", post.plainText);
			if(!exists) {
				continue;
			}
			
			// may exist
			exists = this.bloomFilter.exists("bloom:uniqueID", post.uniqueID);
			if(!exists) {
				continue;
			}
			
			// this post looks like already exists
			// we will remove it
			postIterator.remove();
		}
	}

	@Override
	public void savePosts(List<Post> posts) {
		if (AssertUtils.isEmpty(posts)) {
			return;
		}

		for (Post post : posts) {
			this.insert(post);
			
			// add to bloom filter
			this.bloomFilter.add("bloom:hash", post.hash);
			this.bloomFilter.add("bloom:text", post.plainText);
			this.bloomFilter.add("bloom:uniqueID", post.uniqueID);
			
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
			this.feedTimelineService.addToTimeline(timelineID, List.of(post), false);
			
			post = this.get(postID);
		}
		
		return post;
	}

	private Post unmarkPost(String postID, String fieldName, String timelineID) {
		Post post = this.get(postID);
		if (post != null) {
			this.updateField(post, fieldName, 0l);
			this.feedTimelineService.removePost(timelineID, postID);
			
			post = this.get(postID);
		}
		
		return post;
	}

}
