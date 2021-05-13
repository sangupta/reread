package com.sangupta.reread.entity;

import org.springframework.data.annotation.Id;

/**
 * Stores details about crawling one single feed. This
 * entity is written to DB.
 * 
 * @author sangupta
 *
 */
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
