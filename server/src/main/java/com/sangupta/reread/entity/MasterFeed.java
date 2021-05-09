package com.sangupta.reread.entity;

import org.springframework.data.annotation.Id;

public class MasterFeed {
	
	@Id
	public String feedID;
	
	public String url;
	
	public String siteUrl;
	
	public String normalizedUrl;
	
	public String title;
	
}
