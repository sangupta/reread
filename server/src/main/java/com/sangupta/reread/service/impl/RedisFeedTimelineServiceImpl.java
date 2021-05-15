package com.sangupta.reread.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.TimelineSortOption;
import com.sangupta.reread.service.FeedTimelineService;

/**
 * {@link RedisTemplate} based implementation of the
 * {@link FeedTimelineService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisFeedTimelineServiceImpl implements FeedTimelineService {

	public static final long PAGE_SIZE = 10;

	@Autowired
	protected RedisTemplate<String, String> redisTemplate;

	@Override
	public void updateTimeline(String feedID, Post post) {
		this.redisTemplate.opsForZSet().add(feedID, post.feedPostID, post.updated);	
	}

	@Override
	public void updateTimeline(String feedID, List<Post> posts) {
		for (Post post : posts) {
			this.redisTemplate.opsForZSet().add(feedID, post.feedPostID, post.updated);
		}
	}

	@Override
	public Collection<String> getTimeLine(String feedID) {
		return this.getTimeLine(feedID, TimelineSortOption.NEWEST, null);
	}

	@Override
	public Collection<String> getTimeLine(String feedID, TimelineSortOption sortOption) {
		return this.getTimeLine(feedID, sortOption, null);
	}

	@Override
	public Collection<String> getTimeLine(String timeLineID, TimelineSortOption sortOption, String afterPostID) {
		long rank = 0;
		if(AssertUtils.isNotEmpty(afterPostID)) {
			Long num = this.redisTemplate.opsForZSet().rank(timeLineID, afterPostID);
			if(num != null) {
				rank = num.longValue();
			}
		}

		if (sortOption == TimelineSortOption.OLDEST) {
			return this.redisTemplate.opsForZSet().range(timeLineID, rank + 1, rank + PAGE_SIZE);
		}

		if(AssertUtils.isNotEmpty(afterPostID)) {
			long total = this.size(timeLineID);
			rank = total - rank;
		}
		
		return this.redisTemplate.opsForZSet().reverseRange(timeLineID, rank + 1, rank + PAGE_SIZE);
	}

	@Override
	public String getLatestID(String feedID) {
		Set<String> set = this.redisTemplate.opsForZSet().range(feedID, -1, -1);
		if (AssertUtils.isNotEmpty(set)) {
			return set.iterator().next();
		}

		return null;
	}

	@Override
	public long size(String feedID) {
		Long num = this.redisTemplate.opsForZSet().zCard(feedID);
		if (num == null) {
			return 0;
		}

		return num.longValue();
	}

	@Override
	public void removePost(String timelineID, String id) {
		this.redisTemplate.opsForZSet().remove(timelineID, id);
	}

	@Override
	public void removePosts(String timeLineForFolder, Collection<String> ids) {
		this.redisTemplate.opsForZSet().remove(timeLineForFolder, ids);
	}

}
