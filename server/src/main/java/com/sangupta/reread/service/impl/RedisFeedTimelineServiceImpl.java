package com.sangupta.reread.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.Post;
import com.sangupta.reread.service.FeedTimelineService;

/**
 * {@link RedisTemplate} based implementation of the {@link FeedTimelineService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisFeedTimelineServiceImpl implements FeedTimelineService {
	
	public static final long PAGE_SIZE = 50;
	
	@Autowired
	protected RedisTemplate<String, String> redisTemplate;

	@Override
	public void updateTimeline(String feedID, List<Post> posts) {
		final ListOperations<String, String> listOperation = this.redisTemplate.opsForList();

		Collections.sort(posts);

		// add each element to the feed
		for (int index = posts.size() - 1; index >= 0; index--) {
			Post post = posts.get(index);
			listOperation.leftPush(KEY + feedID, post.feedPostID);
		}
	}

	@Override
	public List<String> getTimeLine(String feedID) {
		final ListOperations<String, String> listOperation = this.redisTemplate.opsForList();
		final String key = KEY + feedID;
		
		final List<String> list = listOperation.range(key, 0, -1);		
		return list;
	}

	@Override
	public void updateAllTimeline(List<Post> posts) {
		this.updateTimeline(ALL_TIMELINE_ID, posts);
	}
	
	@Override
	public Set<String> getSpecialTimeline(String timelineID) {
		return this.redisTemplate.opsForSet().members(SORTED_TIMELINE + timelineID);
	}

	public void addToSpecialTimeline(String timelineID, String postID, long time) {
		this.redisTemplate.opsForSet().add(SORTED_TIMELINE + timelineID, postID);
	}
	
	public void removeFromSpecialTimeline(String timelineID, String postID) {
		this.redisTemplate.opsForSet().remove(SORTED_TIMELINE + timelineID, postID);
	}
	
	@Override
	public String getLatestID(String feedID) {
		final ListOperations<String, String> listOperation = this.redisTemplate.opsForList();
		final String key = KEY + feedID;
		return listOperation.index(key, 0);
	}

	@Override
	public long size(String feedID) {
		final ListOperations<String, String> listOperation = this.redisTemplate.opsForList();
		final String key = KEY + feedID;
		Long num = listOperation.size(key);
		if(num == null) {
			return 0;
		}
		
		return num.longValue();
	}

	@Override
	public void removePost(String timelineID, String id) {
		final ListOperations<String, String> listOperation = this.redisTemplate.opsForList();
		listOperation.remove(timelineID, 0, id);
	}
	
}
