package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.HashUtils;
import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.PostService;

@Service
public class RedisPostServiceImpl extends RedisDataStoreServiceImpl<Post> implements PostService {

	@Override
	public void filterAlreadyExistingPosts(ParsedFeed parsedFeed) {
		if(parsedFeed == null) {
			return;
		}
		
		if(AssertUtils.isEmpty(parsedFeed.posts)) {
			return;
		}
		
		// compute the hash
		for(Post post : parsedFeed.posts) {
			post.hash = HashUtils.getMD5Hex(post.content);
		}
	}

	@Override
	public void savePosts(List<Post> posts) {
		if(AssertUtils.isEmpty(posts)) {
			return;
		}
		
		for(Post post : posts) {
			this.insert(post);
		}
	}

}
