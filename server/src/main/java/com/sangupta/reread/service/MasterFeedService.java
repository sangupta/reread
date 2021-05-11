package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.MasterFeed;

public interface MasterFeedService extends DataStoreService<MasterFeed> {

	public MasterFeed getOrCreateFeedForUrl(String url);
	
	public MasterFeed getOrCreateFeedForUrl(String title, String url);
	
	public MasterFeed getOrCreateFeedForUrl(List<MasterFeed> masterFeeds, String title, String url);

}
