package com.sangupta.reread.entity;

public class UserFeed {

	public String masterFeedID;
	
	public String title;
	
	public long totalPosts;
	
	public long unreadCount;
	
	public UserFeed(MasterFeed feed) {
		this.masterFeedID = feed.feedID;
		this.title = feed.title;
	}

}
