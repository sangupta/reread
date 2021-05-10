package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.entity.Post;

public interface PostService extends DataStoreService<Post>{

	public void filterAlreadyExistingPosts(ParsedFeed parsedFeed);

	public void savePosts(List<Post> posts);

	public Post markRead(String postID);
	
	public Post markUnread(String postID);

	public Post starPost(String postID);

	public Post unstarPost(String postID);

	public Post bookmarkPost(String postID);

	public Post unbookmarkPost(String postID);
	
}
