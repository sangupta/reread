package com.sangupta.reread.entity;

/**
 * Entity holding information about one feed discovered from 
 * a given URL. It is not written to DB.
 * 
 * @author sangupta
 *
 */
public class DiscoveredFeed {
	
	public String feedUrl;
	
	public String feedUrlWithoutScheme;
	
	public String title;
	
	public String siteUrl;
	
	public String iconUrl;
	
	public String rel;
	
	public String type;

	public DiscoveredFeed(String url, String title, String type) {
		this.feedUrl = url;
		this.title = title;
		this.type = type;
	}
}
