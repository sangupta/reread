package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.redislabs.modules.rejson.JReJSON;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.redis.RedisJsonDataStoreServiceImpl;
import com.sangupta.reread.service.MasterFeedService;

/**
 * Redis {@link JReJSON} based implementation of the {@link MasterFeedService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisMasterFeedServiceImpl extends RedisJsonDataStoreServiceImpl<MasterFeed> implements MasterFeedService {

	@Override
	public MasterFeed getOrCreateFeed(MasterFeed mf) {
		return this.getOrCreateInternal(null, mf);
	}
	
	@Override
	public MasterFeed getOrCreateFeed(List<MasterFeed> masterFeeds, MasterFeed mf) {
		return this.getOrCreateInternal(masterFeeds, mf);
	}

	public MasterFeed getOrCreateFeedForUrl(List<MasterFeed> masterFeeds, String title, String url) {
		MasterFeed mf = new MasterFeed(url);
		mf.title = title;
		
		return this.getOrCreateInternal(null, mf);
	}

	protected MasterFeed getOrCreateInternal(List<MasterFeed> masterFeeds, MasterFeed feedToAdd) {
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
	
	@Override
	public boolean insert(MasterFeed entity) {
		entity.added = System.currentTimeMillis();
		return super.insert(entity);
	}

}
