package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;

import com.sangupta.jerry.util.AssertUtils;

public class UserFeedFolder {
	
	public String folderID;
	
	public String title;
	
	public final List<UserFeed> childFeeds = new ArrayList<>();

	public boolean containsFeed(String feedID) {
		if(AssertUtils.isEmpty(feedID)) {
			return false;
		}
		
		for(UserFeed feed : this.childFeeds) {
			if(feed.masterFeedID.equals(feedID)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean addFeed(MasterFeed feed) {
		if(this.containsFeed(feed.feedID)) {
			return false;
		}
		
		this.childFeeds.add(new UserFeed(feed));
		return true;
	}

	public boolean removeFeed(String feedID) {
		return this.childFeeds.removeIf(feed -> (feedID.equalsIgnoreCase(feed.masterFeedID)));
	}

}
