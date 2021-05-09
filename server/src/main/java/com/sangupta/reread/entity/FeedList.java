package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class FeedList {
	
	@Id
	public String userID;

	public final List<UserFeedFolder> folders = new ArrayList<>();
	
	public final List<UserFeed> feeds = new ArrayList<>();

	public boolean containsFeed(String feedID) {
		for(UserFeedFolder folder : this.folders) {
			boolean found = folder.containsFeed(feedID);
			if(found) {
				return true;
			}
		}
		
		for(UserFeed feed : this.feeds) {
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
		
		this.feeds.add(new UserFeed(feed));
		return true;
	}

	public boolean removeFeed(String feedID) {
		for(UserFeedFolder folder : this.folders) {
			boolean removed = folder.removeFeed(feedID);
			if(removed) {
				return true;
			}
		}
		
		return this.feeds.removeIf(feed -> (feedID.equalsIgnoreCase(feed.masterFeedID)));
	}

	public UserFeedFolder getFolder(String folderID) {
		if(folderID == null) {
			return null;
		}
		
		for(UserFeedFolder folder : this.folders) {
			if(folder.folderID.equals(folderID)) {
				return folder;
			}
		}
		
		return null;
	}
	
}
