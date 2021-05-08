package com.sangupta.reread.entity;

import org.springframework.data.annotation.Id;

public class Post {
	
	@Id
	public String feedPostID;
	
	public String masterFeedID;
	
	public PostAuthor author;
	
	public String title;
	
	public String content;
	
	public String snippet;
	
	public PostImage image;
	
	public String link;
	
	public long updated;
	
	public String hash;

}
