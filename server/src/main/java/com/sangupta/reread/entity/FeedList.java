package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.sangupta.jerry.util.AssertUtils;

/**
 * Stores entire feed list for a given user as individual feeds
 * as well as in folders. Entity is written to DB.
 * 
 * @author sangupta
 *
 */
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

	public UserFeedFolder getOrCreateFolder(String title) {
		if(AssertUtils.isEmpty(title)) {
			return null;
		}
		
		for(UserFeedFolder folder : this.folders) {
			if(folder.title.equals(title)) {
				return folder;
			}
		}
		
		UserFeedFolder folder = new UserFeedFolder();
		
		folder.folderID = UUID.randomUUID().toString();
		folder.title = title;
		
		this.folders.add(folder);
		return folder;
	}
	
}
