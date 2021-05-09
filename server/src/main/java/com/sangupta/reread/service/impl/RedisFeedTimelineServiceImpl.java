package com.sangupta.reread.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.Post;
import com.sangupta.reread.service.FeedTimelineService;

@Service
public class RedisFeedTimelineServiceImpl implements FeedTimelineService {

	public static final String KEY = "timeline:";

	protected static final ScanOptions DEFAULT_SCAN_OPTIONS = ScanOptions.scanOptions().count(50).build();

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
		final List<String> list = listOperation.range(KEY + feedID, 0, 50);

		return list;
	}

}
