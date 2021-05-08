package com.sangupta.reread.entity;

public class Feed {
	
	public String url;
	
	public String urlWithoutScheme;
	
	public String title;
	
	public String rel;
	
	public String type;

	public Feed(String url, String title, String type) {
		this.url = url;
		this.title = title;
		this.type = type;
	}
}
