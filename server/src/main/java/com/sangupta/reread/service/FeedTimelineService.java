package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.Post;

public interface FeedTimelineService {
	
	public static final String KEY = "timeline:";

	public static final String ALL_TIMELINE_ID = "$all";

	public List<String> getTimeLine(String feedID);

	public void updateAllTimeline(List<Post> posts);

	public void updateTimeline(String feedID, List<Post> posts);

}
