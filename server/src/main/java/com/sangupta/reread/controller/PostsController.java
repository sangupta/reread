package com.sangupta.reread.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.UserFeed;
import com.sangupta.reread.entity.UserFeedFolder;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.PostSearchService;
import com.sangupta.reread.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostsController {

	@Autowired
	protected FeedTimelineService feedTimelineService;

	@Autowired
	protected PostService postService;

	@Autowired
	protected FeedListService feedListService;
	
	@Autowired
	protected PostSearchService postSearchService;

	@GetMapping("/all")
	public List<Post> getAllPosts() {
		List<Post> posts = new ArrayList<>();
		this.addPostsForTimeline(posts, FeedTimelineService.ALL_TIMELINE_ID);
		return posts;
	}
	
	@GetMapping("/stars")
	public List<Post> getStarredPosts() {
		List<Post> posts = new ArrayList<>();
		this.addPostsForTimeline(posts, FeedTimelineService.STARRED_TIMELINE_ID);
		return posts;
	}
	
	@GetMapping("/bookmarks")
	public List<Post> getBookmarkedPosts() {
		List<Post> posts = new ArrayList<>();
		this.addPostsForTimeline(posts, FeedTimelineService.BOOKMARK_TIMELINE_ID);
		return posts;
	}

	@GetMapping("/feed/{feedID}")
	public List<Post> getFeedPosts(@PathVariable String feedID) {
		List<Post> posts = new ArrayList<>();

		this.addPostsForTimeline(posts, feedID);

		return posts;
	}
	
	@GetMapping("/read/{postID}")
	public Post markPostRead(@PathVariable String postID) {
		return this.postService.markRead(postID);
	}
	
	@GetMapping("/unread/{postID}")
	public Post markPostUnread(@PathVariable String postID) {
		return this.postService.markUnread(postID);
	}

	@GetMapping("/folder/{folderID}")
	public List<Post> getFolderPosts(@PathVariable String folderID) {
		FeedList feedList = this.feedListService.get(SecurityContext.getUserID());
		if (feedList == null) {
			return null;
		}

		UserFeedFolder folder = feedList.getFolder(folderID);
		if (folder == null) {
			return null;
		}

		List<Post> posts = new ArrayList<>();
		for (UserFeed feed : folder.childFeeds) {
			this.addPostsForTimeline(posts, feed.masterFeedID);
		}
		
		Collections.sort(posts);
		return posts;
	}
	
	@PostMapping("/markAllRead")
	public List<Post> markAllRead(@RequestBody List<String> ids) {
		if(AssertUtils.isEmpty(ids)) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}
		
		List<Post> posts = new ArrayList<>();
		for(String id : ids) {
			Post post = this.postService.markRead(id);
			if(post != null) {
				posts.add(post);
			}
		}
		
		return posts;
	}
	
	@PostMapping("/markAllUnread")
	public List<Post> markAllUnread(@RequestBody List<String> ids) {
		if(AssertUtils.isEmpty(ids)) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}
		
		List<Post> posts = new ArrayList<>();
		for(String id : ids) {			
			Post post = this.postService.markUnread(id);
			if(post != null) {
				posts.add(post);
			}
		}
		
		return posts;
	}
	
	@GetMapping("/search")
	public List<Post> search(@RequestParam String query) {
		return this.postSearchService.search(query);
	}
	
	protected void addPostsForTimeline(List<Post> posts, String timelineID) {
		List<String> timeline = this.feedTimelineService.getTimeLine(timelineID);
		if (AssertUtils.isEmpty(timeline)) {
			return;
		}

		for (String postID : timeline) {
			Post post = this.postService.get(postID);
			if(post != null) {
				posts.add(post);
			}
		}
	}

}
