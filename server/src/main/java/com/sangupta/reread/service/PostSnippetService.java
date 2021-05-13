package com.sangupta.reread.service;

import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.entity.Post;

/**
 * Service that allows generation of post snippets.
 * 
 * @author sangupta
 *
 */
public interface PostSnippetService {

	/**
	 * Create a snippet from {@link Post} instance using the
	 * base URL provided in {@link ParsedFeed#siteUrl}.
	 * 
	 * @param siteLinkInFeed
	 * @param post
	 */
	public void createSnippet(String siteLinkInFeed, Post post);

}
