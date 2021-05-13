package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.Post;

/**
 * Service that persists {@link Post} in the data store.
 * 
 * @author sangupta
 *
 */
public interface PostService extends DataStoreService<Post>{

	/**
	 * Filter the existing {@link List} of {@link Post}s to remove
	 * posts that are already present with same content.
	 * 
	 * @param posts
	 */
	public void filterAlreadyExistingPosts(List<Post> posts);

	/**
	 * Save a {@link List} of {@link Post}s in the data store.
	 * 
	 * @param posts
	 */
	public void savePosts(List<Post> posts);

	/**
	 * Mark the given post as read.
	 * 
	 * @param postID
	 * @return
	 */
	public Post markRead(String postID);
	
	/**
	 * Mark the given post as unread.
	 * 
	 * @param postID
	 * @return
	 */
	public Post markUnread(String postID);

	/**
	 * Star the post for user.
	 * 
	 * @param postID
	 * @return
	 */
	public Post starPost(String postID);

	/**
	 * Unstar the post for user.
	 * 
	 * @param postID
	 * @return
	 */
	public Post unstarPost(String postID);

	/**
	 * Bookmark the post for user.
	 * 
	 * @param postID
	 * @return
	 */
	public Post bookmarkPost(String postID);

	/**
	 * Unbookmark the post for user.
	 * 
	 * @param postID
	 * @return
	 */
	public Post unbookmarkPost(String postID);
	
}
