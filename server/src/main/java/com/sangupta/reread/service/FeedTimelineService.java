package com.sangupta.reread.service;

import java.util.Collection;
import java.util.List;

import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.TimelineSortOption;
import com.sangupta.reread.entity.UserFeedFolder;

/**
 * Service that provides access to a user or feeds timeline.
 * 
 * @author sangupta
 *
 */
public interface FeedTimelineService {
	
	public static final String TIMELINE = "timeline-";
	
	public static final String ALL_TIMELINE_ID = "$all";
	
	public static final String READ_TIMELINE_ID = "$read";
	
	public static final String STARRED_TIMELINE_ID = "$starred";
	
	public static final String BOOKMARK_TIMELINE_ID = "$bookmark";

	/**
	 * Update the timeline for given feed based on the given {@link List} of {@link Post}s
	 * 
	 * @param feedID
	 * @param posts
	 */
	public void addToTimeline(String feedID, List<Post> posts, boolean forceUpdateAllTimeline);
	
	/**
	 * Return the list of post IDs that comprise a given timeline.
	 * 
	 * @param timeLineID
	 * @return
	 */
	public Collection<String> getTimeLine(String timeLineID);
	
	public Collection<String> getTimeLine(String timeLineID, TimelineSortOption option, String afterPostID);
	
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

	public void removeTimeline(String feedID, UserFeedFolder folder);

	public void removePost(String timeLineID, String id);

	public static String getTimeLineForFolder(String folderID) {
		return "timeline-folder:" + folderID;
	}

	public String getOldestPostID(String feedID);

}
