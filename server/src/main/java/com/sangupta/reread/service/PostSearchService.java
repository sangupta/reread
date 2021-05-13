package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.Post;

/**
 * Service that provides the search functionality.
 *
 * @author sangupta
 *
 */
public interface PostSearchService {
	
	/**
	 * Index the post in the search engine.
	 * 
	 * @param post
	 */
	public void indexPost(Post post);
	
	/**
	 * Return a {@link List} of {@link Post}s that contain the given
	 * text query.
	 * 
	 * @param text
	 * @return
	 */
	public List<Post> search(String text);

}
