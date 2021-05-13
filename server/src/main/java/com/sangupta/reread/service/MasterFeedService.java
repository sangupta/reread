package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.MasterFeed;

/**
 * Service that allows persisting {@link MasterFeed} in data store.
 * 
 * @author sangupta
 *
 */
public interface MasterFeedService extends DataStoreService<MasterFeed> {

	/**
	 * Return an existing or create a new {@link MasterFeed} based on given details.
	 * 
	 * @param mf
	 * @return
	 */
	public MasterFeed getOrCreateFeed(MasterFeed mf);
	
	/**
	 * Return an existing or create a new {@link MasterFeed} based on given details, using
	 * the given {@link List} as cache than checking in DB directly.
	 * 
	 * @param masterFeeds
	 * @param mf
	 * @return
	 */
	public MasterFeed getOrCreateFeed(List<MasterFeed> masterFeeds, MasterFeed mf);
	
	/**
	 * Return an existing or create a new {@link MasterFeed} based on given details, using
	 * the given {@link List} as cache than checking in DB directly.
	 * 
	 * @param masterFeeds
	 * @param title
	 * @param url
	 * @return
	 */
	public MasterFeed getOrCreateFeedForUrl(List<MasterFeed> masterFeeds, String title, String url);
	
}
