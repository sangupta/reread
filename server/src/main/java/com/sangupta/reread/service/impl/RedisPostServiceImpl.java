package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.PostService;

@Service
public class RedisPostServiceImpl extends RedisDataStoreServiceImpl<Post> implements PostService {

	@Override
	public void filterAlreadyExistingPosts(ParsedFeed parsedFeed) {

	}

	@Override
	public void savePosts(List<Post> posts) {

	}

}
