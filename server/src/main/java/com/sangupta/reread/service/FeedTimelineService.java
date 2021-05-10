package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.TimelineSortOption;

public interface FeedTimelineService {
	
	public static final String KEY = "timeline:";
	
	public static final String SORTED_TIMELINE = "sortedTimeline:";

	public static final String ALL_TIMELINE_ID = "$all";
	
	public static final String READ_TIMELINE_ID = "$read";
	
	public static final String STARRED_TIMELINE_ID = "$starred";
	
	public static final String BOOKMARK_TIMELINE_ID = "$bookmark";

	public List<String> getTimeLine(String feedID, TimelineSortOption sortOption, String lastPostID);

	public void updateAllTimeline(List<Post> posts);

	public void updateTimeline(String feedID, List<Post> posts);
	
	public void addToSpecialTimeline(String timelineID, String postID, long time);
	
	public void removeFromSpecialTimeline(String timelineID, String postID);

}
