package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.redislabs.modules.rejson.JReJSON;
import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.FeedCrawlDetailsService;

/**
 * Redis {@link JReJSON} based implementation of the {@link FeedCrawlDetailsService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisFeedCrawlDetailsServiceImpl extends RedisDataStoreServiceImpl<FeedCrawlDetails> implements FeedCrawlDetailsService {

}
