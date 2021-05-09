package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.Post;

public interface FeedTimelineService {

	public void updateTimeline(String feedID, List<Post> posts);

	public List<String> getTimeLine(String feedID);

}
