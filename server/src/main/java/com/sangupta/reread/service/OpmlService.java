package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.OpmlFeed;

public interface OpmlService {
	
	public List<OpmlFeed> parseOpml(String opmlContents);

	public List<MasterFeed> importFeeds(List<OpmlFeed> feeds);

}
