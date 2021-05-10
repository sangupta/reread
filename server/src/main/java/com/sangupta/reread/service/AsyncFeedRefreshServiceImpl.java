package com.sangupta.reread.service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.MasterFeed;

@Service
public class AsyncFeedRefreshServiceImpl implements FeedRefreshService {
	
	private static final Queue<String> QUEUE = new LinkedBlockingQueue<>();

	@Override
	public boolean refreshFeed(MasterFeed feed) {
		if(feed == null) {
			return false;
		}
		
		return QUEUE.add(feed.feedID);
	}

	@Override
	public boolean refreshFeeds(List<MasterFeed> feeds) {
		boolean refreshed = true;
		for(MasterFeed feed : feeds) {
			refreshed = refreshed & this.refreshFeed(feed);
		}
		
		return refreshed;
	}

}
