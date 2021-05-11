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
//		final ListOperations<String, String> listOperation = this.redisTemplate.opsForList();
//		final ZSetOperations<String, String> setOperations = this.redisTemplate.opsForZSet();
//
//		// add to sorted set
//		for (Post post : posts) {
//			setOperations.add(SORTED_TIMELINE + ALL_TIMELINE_ID, post.feedPostID, post.updated);
//		}
//
//		// now get all these ids and add them to a normal timeline
//		long cardinality = setOperations.zCard(SORTED_TIMELINE + ALL_TIMELINE_ID);
//		ScanOptions scanOptions = ScanOptions.scanOptions().count(cardinality).build();
//		Cursor<TypedTuple<String>> cursor = setOperations.scan(SORTED_TIMELINE + ALL_TIMELINE_ID, scanOptions);
//		List<String> ids = new ArrayList<>();
//		while (cursor.hasNext()) {
//			String id = cursor.next().getValue();
//			ids.add(id);
//		}
//
//		String tempKey = KEY + ALL_TIMELINE_ID + "temp";
//		listOperation.leftPushAll(tempKey, ids);
//		this.redisTemplate.rename(tempKey, KEY + ALL_TIMELINE_ID);
//		this.redisTemplate.delete(tempKey);
		
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
	
}
