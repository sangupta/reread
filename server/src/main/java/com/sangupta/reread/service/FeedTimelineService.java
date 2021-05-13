package com.sangupta.reread.service;

import java.util.List;
import java.util.Set;

import com.sangupta.reread.entity.Post;

/**
 * Service that provides access to a user or feeds timeline.
 * 
 * @author sangupta
 *
 */
public interface FeedTimelineService {
	
	public static final String KEY = "timeline:";
	
	public static final String SORTED_TIMELINE = "setTimeline:";

	public static final String ALL_TIMELINE_ID = "$all";
	
	public static final String READ_TIMELINE_ID = "$read";
	
	public static final String STARRED_TIMELINE_ID = "$starred";
	
	public static final String BOOKMARK_TIMELINE_ID = "$bookmark";

	/**
	 * Return the list of post IDs that comprise a given timeline.
	 * 
	 * @param timeLineID
	 * @return
	 */
	public List<String> getTimeLine(String timeLineID);

	/**
	 * Update the timeline for all feeds based on the given {@link List} of {@link Post}s
	 * 
	 * @param posts
	 */
	public void updateAllTimeline(List<Post> posts);

	/**
	 * Update the timeline for given feed based on the given {@link List} of {@link Post}s
	 * 
	 * @param feedID
	 * @param posts
	 */
	public void updateTimeline(String feedID, List<Post> posts);
	
	/**
	 * Get the special timeline such as all feeds, stars or bookmarks.
	 * 
	 * @param timelineID
	 * @return
	 */
	public Set<String> getSpecialTimeline(String timelineID);
	
	/**
	 * Add the post ID to a special timeline such as all feeds, stars or bookmarks.
	 * 
	 * @param timelineID
	 * @param postID
	 * @param time
	 */
	public void addToSpecialTimeline(String timelineID, String postID, long time);
	
	/**
	 * Remove the post ID from the special timeline.
	 * 
	 * @param timelineID
	 * @param postID
	 */
	public void removeFromSpecialTimeline(String timelineID, String postID);

	/**
	 * Get the post ID for the most recent post in the timeline, if present.
	 * 
	 * @param feedID
	 * @return
	 */
	public String getLatestID(String feedID);

	/**
	 * Get the number of posts in the given timeline.
	 * 
	 * @param feedID
	 * @return
	 */
	public long size(String feedID);

}
