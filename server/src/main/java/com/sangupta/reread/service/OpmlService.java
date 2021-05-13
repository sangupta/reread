package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.OpmlFeed;

/**
 * Service that allows parsing OPML files.
 * 
 * @author sangupta
 *
 */
public interface OpmlService {
	
	/**
	 * Parse an OPML file and return a {@link List} of {@link OpmlFeed}s.
	 * 
	 * @param opmlContents
	 * @return
	 */
	public List<OpmlFeed> parseOpml(String opmlContents);

	/**
	 * Given a list of {@link OpmlFeed}s return a list of {@link MasterFeed} instances.
	 * 
	 * @param feeds
	 * @return
	 */
	public List<MasterFeed> importFeeds(List<OpmlFeed> feeds);

}
