package com.sangupta.reread.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.PostIncludeOption;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.TimelineSortOption;
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
	public List<Post> getAllPosts(@RequestParam(required = false) TimelineSortOption sort, @RequestParam(required = false) PostIncludeOption include, @RequestParam(required = false) String lastPostID) {
		List<Post> posts = new ArrayList<>();
		this.addPostsForTimeline(posts, FeedTimelineService.ALL_TIMELINE_ID, sort, include, lastPostID);
		return posts;
	}
	
	@GetMapping("/stars")
	public List<Post> getStarredPosts(@RequestParam(required = false) TimelineSortOption sort, @RequestParam(required = false) PostIncludeOption include, @RequestParam(required = false) String lastPostID) {
		List<Post> posts = new ArrayList<>();
		this.addPostsForTimeline(posts, FeedTimelineService.STARRED_TIMELINE_ID, sort, include, lastPostID);
		return posts;
	}
	
	@GetMapping("/bookmarks")
	public List<Post> getBookmarkedPosts(@RequestParam(required = false) TimelineSortOption sort, @RequestParam(required = false) PostIncludeOption include, @RequestParam(required = false) String lastPostID) {
		List<Post> posts = new ArrayList<>();
		this.addPostsForTimeline(posts, FeedTimelineService.BOOKMARK_TIMELINE_ID, sort, include, lastPostID);
		return posts;
	}

	@GetMapping("/feed/{feedID}")
	public List<Post> getFeedPosts(@PathVariable String feedID, @RequestParam(required = false) TimelineSortOption sort, @RequestParam(required = false) PostIncludeOption include, @RequestParam(required = false) String lastPostID) {
		List<Post> posts = new ArrayList<>();

		this.addPostsForTimeline(posts, feedID, sort, include, lastPostID);

		return posts;
	}
	
	@GetMapping("/read/{postID}")
	public String markPostRead(@PathVariable String postID) {
		this.postService.markRead(postID);
		return postID;
	}
	
	@GetMapping("/unread/{postID}")
	public String markPostUnread(@PathVariable String postID) {
		this.postService.markUnread(postID);
		return postID;
	}

	@GetMapping("/folder/{folderID}")
	public List<Post> getFolderPosts(@PathVariable String folderID, @RequestParam(required = false) TimelineSortOption sort, @RequestParam(required = false) PostIncludeOption include, @RequestParam(required = false) String lastPostID) {
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
			this.addPostsForTimeline(posts, feed.masterFeedID, sort, include, lastPostID);
		}
		
		Collections.sort(posts);
		
		return posts;
	}
	
	@GetMapping("/search")
	public List<Post> search(@RequestParam String query) {
		return this.postSearchService.search(query);
	}
	
	protected void addPostsForTimeline(List<Post> posts, String timelineID, TimelineSortOption sort, PostIncludeOption include, String lastPostID) {
		if(include == null) {
			include = PostIncludeOption.ALL;
		}
		
		List<String> timeline = this.feedTimelineService.getTimeLine(timelineID, sort, lastPostID);
		if (AssertUtils.isEmpty(timeline)) {
			return;
		}

		for (String postID : timeline) {
			Post post = this.postService.get(postID);
			if(post != null) {
				if(include == PostIncludeOption.ALL || (include == PostIncludeOption.UNREAD && post.readOn == 0)) {
					posts.add(post);
				}
			}
		}
		
		if(sort != TimelineSortOption.NEWEST) {
			Collections.sort(posts, Collections.reverseOrder());
		}
	}

}
