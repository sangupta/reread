package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.MasterFeed;

public interface FeedRefreshService {

	public boolean refreshFeed(MasterFeed feed);
	
	public boolean refreshFeeds(List<MasterFeed> feeds);
	
}
