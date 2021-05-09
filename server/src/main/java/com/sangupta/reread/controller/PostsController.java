package com.sangupta.reread.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostsController {
	
	@Autowired
	protected FeedTimelineService feedTimelineService;
	
	@Autowired
	protected PostService postService;
	
	@GetMapping("/feed/{feedID}")
	public List<Post> getFeedPosts(@PathVariable String feedID) {
		List<Post> posts = new ArrayList<>();
		
		List<String> timeline = this.feedTimelineService.getTimeLine(feedID);
		if(AssertUtils.isEmpty(timeline)) {
			return posts;
		}
		
		for(String postID : timeline) {
			posts.add(this.postService.get(postID));
		}
		
		return posts;
	}

}
