package com.sangupta.reread.entity;

/**
 * Image details inside the {@link Post}. This is shown
 * in cards/masonry layouts etc.
 * 
 * @author sangupta
 *
 */
public class PostImage {
	
	public String url;
	
	public int height;
	
	public int width;
	
	public PostImage() {
		
	}
	
	public PostImage(String url, int width, int height) {
		this.url = url;
		this.width = width;
		this.height = height;
	}

}
