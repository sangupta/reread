package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.FeedCrawlDetailsService;

@Service
public class RedisFeedCrawlDetailsServiceImpl extends RedisDataStoreServiceImpl<FeedCrawlDetails> implements FeedCrawlDetailsService {

}
