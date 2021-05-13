package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Object constructed after parsing a feed from URL
 * response. Entity is not written to DB.
 * 
 * @author sangupta
 *
 */
public class ParsedFeed {

	public String feedTitle;

	public String siteUrl;

	public String nextUrl;
	
	public long crawlTime;

	public long lastModifiedTimestamp;

	public String lastModifiedHeader;

	public String eTagHeader;
	
	public final List<Post> posts = new ArrayList<>();

	public void sortPosts() {
		Collections.sort(this.posts);
	}

}
