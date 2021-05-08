package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;

public class FeedList {
	
	public String userID;

	public final List<UserFeedFolder> folders = new ArrayList<>();
	
	public final List<UserFeed> feeds = new ArrayList<>();
	
}
