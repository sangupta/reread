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
	public MasterFeed getOrCreateFeedForUrl(MasterFeed mf) {
		return this.getOrCreateFeedForUrl(null, mf);
	}

	public MasterFeed getOrCreateFeedForUrl(List<MasterFeed> masterFeeds, String title, String url) {
		MasterFeed mf = new MasterFeed(url);
		mf.title = title;
		
		return this.getOrCreateFeedForUrl(null, mf);
	}

	protected MasterFeed getOrCreateFeedForUrl(List<MasterFeed> masterFeeds, MasterFeed feedToAdd) {
		if (masterFeeds == null) {
			masterFeeds = this.getAll();
		}

		if (AssertUtils.isNotEmpty(masterFeeds)) {
			for (MasterFeed feed : masterFeeds) {
				if (feed.normalizedUrl.equals(feedToAdd.normalizedUrl)) {
					return feed;
				}
			}
		}

		// insert this one
		feedToAdd.feedID = null;
		this.insert(feedToAdd);
		return feedToAdd;
	}

}
