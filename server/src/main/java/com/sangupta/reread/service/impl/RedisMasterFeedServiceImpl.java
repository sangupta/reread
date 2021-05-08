package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.UrlCanonicalizer;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.MasterFeedService;

@Service
public class RedisMasterFeedServiceImpl extends RedisDataStoreServiceImpl<MasterFeed> implements MasterFeedService {

	@Override
	public MasterFeed getOrCreateFeedForUrl(String url) {
		url = UrlCanonicalizer.canonicalize(url);

		List<MasterFeed> all = this.getAll();

		if (AssertUtils.isNotEmpty(all)) {
			for (MasterFeed feed : all) {
				if (feed.url.equals(url)) {
					return feed;
				}
			}
		}
		
		MasterFeed feed = new MasterFeed();
		feed.url = url;
		this.insert(feed);
		return feed;
	}

}
