package com.sangupta.reread.entity;

import org.springframework.data.annotation.Id;

public class FeedCrawlDetails {

	@Id
	public String feedID;
	
	public long lastCrawled;
	
	public String lastModifiedHeader;
	
	public long lastModifiedTime;
	
	public String etag;

	public long numCrawled;
	
	public long numUpdated;
	
	public long numPosts;
	
	public String latestPostID;
	
}
