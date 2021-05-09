package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.Post;

public interface PostSearchService {
	
	public void indexPost(Post post);
	
	public List<Post> search(String text);

}
