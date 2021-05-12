package com.sangupta.reread.entity;

public class UserFeed {

	public String masterFeedID;
	
	public String title;
	
	public String website;
	
	public String iconUrl;
	
	public long totalPosts;
	
	public long unreadCount;
	
	public UserFeed(MasterFeed feed) {
		this.masterFeedID = feed.feedID;
		this.title = feed.title;
		this.website = feed.siteUrl;
		this.iconUrl = feed.iconUrl;
	}

}
