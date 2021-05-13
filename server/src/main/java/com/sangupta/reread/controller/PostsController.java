package com.sangupta.reread.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import com.sangupta.reread.entity.UserActivity;
import com.sangupta.reread.entity.UserFeed;
import com.sangupta.reread.entity.UserFeedFolder;
import com.sangupta.reread.service.AnalyticsService;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.FeedTimelineService;
import com.sangupta.reread.service.PostSearchService;
import com.sangupta.reread.service.PostService;

/**
 * REST end points dedicated to all about posts.
 * 
 * @author sangupta
 *
 */
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

	@Autowired
	protected AnalyticsService analyticsService;

	/**
	 * Get posts about the All timeline.
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public List<Post> getAllPosts() {
		List<Post> posts = new ArrayList<>();
		this.addPostsForTimeline(posts, FeedTimelineService.ALL_TIMELINE_ID);
		return posts;
	}

	/**
	 * Get posts about the Stars timeline.
	 * 
	 * @return
	 */
	@GetMapping("/stars")
	public List<Post> getStarredPosts() {
		List<Post> posts = new ArrayList<>();
		Set<String> ids = this.feedTimelineService.getSpecialTimeline(FeedTimelineService.STARRED_TIMELINE_ID);
		this.addPostsForIDs(posts, ids);
		return posts;
	}

	/**
	 * Get posts about the Bookmarks timeline.
	 * 
	 * @return
	 */
	@GetMapping("/bookmarks")
	public List<Post> getBookmarkedPosts() {
		List<Post> posts = new ArrayList<>();
		Set<String> ids = this.feedTimelineService.getSpecialTimeline(FeedTimelineService.BOOKMARK_TIMELINE_ID);
		this.addPostsForIDs(posts, ids);
		return posts;
	}

	/**
	 * Get posts about a single feed.
	 * 
	 * @param feedID
	 * @return
	 */
	@GetMapping("/feed/{feedID}")
	public List<Post> getFeedPosts(@PathVariable String feedID) {
		List<Post> posts = new ArrayList<>();

		this.addPostsForTimeline(posts, feedID);

		return posts;
	}

	/**
	 * Mark a given post as read
	 * 
	 * @param postID
	 * @return
	 */
	@GetMapping("/read/{postID}")
	public Post markPostRead(@PathVariable String postID) {
		Post post = this.postService.markRead(postID);
		this.analyticsService.recordUserActivity(UserActivity.READ, post);
		return post;
	}

	/**
	 * Mark a given post as unread
	 * 
	 * @param postID
	 * @return
	 */
	@GetMapping("/unread/{postID}")
	public Post markPostUnread(@PathVariable String postID) {
		return this.postService.markUnread(postID);
	}

	/**
	 * Star a given post
	 * 
	 * @param postID
	 * @return
	 */
	@GetMapping("/star/{postID}")
	public Post starPost(@PathVariable String postID) {
		Post post = this.postService.starPost(postID);
		this.analyticsService.recordUserActivity(UserActivity.STAR, post);
		return post;
	}

	/**
	 * Unstar the given post
	 * 
	 * @param postID
	 * @return
	 */
	@GetMapping("/unstar/{postID}")
	public Post unstarPost(@PathVariable String postID) {
		return this.postService.unstarPost(postID);
	}

	/**
	 * Bookmark a given post
	 * 
	 * @param postID
	 * @return
	 */
	@GetMapping("/bookmark/{postID}")
	public Post bookmark(@PathVariable String postID) {
		Post post = this.postService.bookmarkPost(postID);
		this.analyticsService.recordUserActivity(UserActivity.BOOKMARK, post);
		return post;
	}

	/**
	 * Unbookmark the given post
	 * 
	 * @param postID
	 * @return
	 */
	@GetMapping("/unbookmark/{postID}")
	public Post unbookmarkPost(@PathVariable String postID) {
		return this.postService.unbookmarkPost(postID);
	}

	/**
	 * Get posts about the folder timeline.
	 * 
	 * @param folderID
	 * @return
	 */
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

	/**
	 * Mark all posts are read.
	 * 
	 * @param ids List of all post IDs that are to be marked as read.
	 * 
	 * @return
	 */
	@PostMapping("/markAllRead")
	public List<Post> markAllRead(@RequestBody List<String> ids) {
		if (AssertUtils.isEmpty(ids)) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}

		List<Post> posts = new ArrayList<>();
		for (String id : ids) {
			Post post = this.postService.markRead(id);
			if (post != null) {
				posts.add(post);
			}
		}

		return posts;
	}
	
	/**
	 * Mark all posts are unread.
	 * 
	 * @param ids List of all post IDs that are to be marked as unread.
	 * @return
	 */
	@PostMapping("/markAllUnread")
	public List<Post> markAllUnread(@RequestBody List<String> ids) {
		if (AssertUtils.isEmpty(ids)) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}

		List<Post> posts = new ArrayList<>();
		for (String id : ids) {
			Post post = this.postService.markUnread(id);
			if (post != null) {
				posts.add(post);
			}
		}

		return posts;
	}

	/**
	 * Search for all posts with a given term.
	 * 
	 * @param query the search text query
	 * 
	 * @return
	 */
	@GetMapping("/search")
	public List<Post> search(@RequestParam String query) {
		return this.postSearchService.search(query);
	}

	/**
	 * Add posts to the list from given timeline ID.
	 * 
	 * @param posts list to add posts to
	 * 
	 * @param timelineID the timeline ID to fetch posts from
	 */
	protected void addPostsForTimeline(List<Post> posts, String timelineID) {
		List<String> timeline = this.feedTimelineService.getTimeLine(timelineID);
		this.addPostsForIDs(posts, timeline);
	}

	/**
	 * Add posts to the list for given post IDs.
	 * 
	 * @param posts list to add posts to
	 * 
	 * @param postIdsToBeAdded ID of each post to be added
	 */
	protected void addPostsForIDs(List<Post> posts, Collection<String> postIdsToBeAdded) {
		if (AssertUtils.isEmpty(postIdsToBeAdded)) {
			return;
		}

		for (String postID : postIdsToBeAdded) {
			Post post = this.postService.get(postID);
			if (post != null) {
				posts.add(post);
			}
		}
	}

}
