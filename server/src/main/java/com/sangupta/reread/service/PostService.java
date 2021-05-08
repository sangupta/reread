package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.entity.Post;

public interface PostService {

	public void filterAlreadyExistingPosts(ParsedFeed parsedFeed);

	public void savePosts(List<Post> posts);

}
