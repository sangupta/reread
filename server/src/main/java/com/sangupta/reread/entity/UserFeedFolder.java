package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;

public class UserFeedFolder {
	
	public String folderID;
	
	public String title;
	
	public final List<UserFeed> childFeeds = new ArrayList<>();

}
