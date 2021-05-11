package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.MasterFeedService;

@Service
public class RedisMasterFeedServiceImpl extends RedisDataStoreServiceImpl<MasterFeed> implements MasterFeedService {

	@Override
	public MasterFeed getOrCreateFeedForUrl(String url) {
		return this.getOrCreateFeedForUrl(url, url);
	}

	@Override
	public MasterFeed getOrCreateFeedForUrl(String title, String url) {
		return this.getOrCreateFeedForUrl(null, title, url);
	}
	
	public MasterFeed getOrCreateFeedForUrl(List<MasterFeed> masterFeeds, String title, String url) {
		String normalizedUrl = MasterFeed.getNormalizedUrl(url);

		if(masterFeeds == null) {
			masterFeeds = this.getAll();
		}

		if (AssertUtils.isNotEmpty(masterFeeds)) {
			for (MasterFeed feed : masterFeeds) {
				if (feed.normalizedUrl.equals(normalizedUrl)) {
					return feed;
				}
			}
		}
		
		MasterFeed feed = new MasterFeed(url);
		feed.title = title;

		this.insert(feed);
		return feed;
	}

}
