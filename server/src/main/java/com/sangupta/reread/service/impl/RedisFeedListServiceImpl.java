package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.FeedListService;

@Service
public class RedisFeedListServiceImpl extends RedisDataStoreServiceImpl<FeedList> implements FeedListService {

}
