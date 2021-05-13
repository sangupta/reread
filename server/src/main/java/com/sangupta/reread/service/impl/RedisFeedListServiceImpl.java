package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.redislabs.modules.rejson.JReJSON;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.FeedListService;

/**
 * Redis {@link JReJSON} based implementation of the {@link FeedListService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisFeedListServiceImpl extends RedisDataStoreServiceImpl<FeedList> implements FeedListService {

	@Override
	public FeedList getOrCreate(String userID) {
		FeedList feedList = this.get(userID);
		
		if(feedList == null) {
			feedList = new FeedList();
			feedList.userID = userID;
			this.update(feedList);
		}
		
		return feedList;
	}

}
