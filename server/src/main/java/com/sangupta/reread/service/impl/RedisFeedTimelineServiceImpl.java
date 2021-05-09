package com.sangupta.reread.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.Post;
import com.sangupta.reread.service.FeedTimelineService;

@Service
public class RedisFeedTimelineServiceImpl implements FeedTimelineService {

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

	@Override
	public void updateAllTimeline(List<Post> posts) {
		final ListOperations<String, String> listOperation = this.redisTemplate.opsForList();
		final ZSetOperations<String, String> setOperations = this.redisTemplate.opsForZSet();

		// add to sorted set
		for (Post post : posts) {
			setOperations.add("sortedTimeline:" + ALL_TIMELINE_ID, post.feedPostID, post.updated);
		}

		// now get all these ids and add them to a normal timeline
		long cardinality = setOperations.zCard("sortedTimeline:" + ALL_TIMELINE_ID);
		ScanOptions scanOptions = ScanOptions.scanOptions().count(cardinality).build();
		Cursor<TypedTuple<String>> cursor = setOperations.scan("sortedTimeline:" + ALL_TIMELINE_ID, scanOptions);
		List<String> ids = new ArrayList<>();
		while (cursor.hasNext()) {
			String id = cursor.next().getValue();
			ids.add(id);
		}

		String tempKey = KEY + ALL_TIMELINE_ID + "temp";
		listOperation.leftPushAll(tempKey, ids);
		this.redisTemplate.rename(tempKey, KEY + ALL_TIMELINE_ID);
		this.redisTemplate.delete(tempKey);
	}

}