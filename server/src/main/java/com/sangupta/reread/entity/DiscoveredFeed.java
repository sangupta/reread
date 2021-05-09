package com.sangupta.reread.entity;

public class DiscoveredFeed {
	
	public String url;
	
	public String urlWithoutScheme;
	
	public String title;
	
	public String rel;
	
	public String type;

	public DiscoveredFeed(String url, String title, String type) {
		this.url = url;
		this.title = title;
		this.type = type;
	}
}
