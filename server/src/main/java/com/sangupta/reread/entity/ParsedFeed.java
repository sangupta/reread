package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;

public class ParsedFeed {

	public String feedTitle;

	public String siteUrl;

	public String nextUrl;
	
	public long crawlTime;

	public long lastModifiedTimestamp;

	public long lastModifiedHeader;

	public String eTagHeader;
	
	public final List<Post> posts = new ArrayList<>();

}
