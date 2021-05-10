package com.sangupta.reread.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.OpmlFeed;
import com.sangupta.reread.entity.UserFeedFolder;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.MasterFeedService;
import com.sangupta.reread.service.OpmlService;
import com.sangupta.reread.utils.OpmlParser;

@Service
public class DefaultOpmlServiceImpl implements OpmlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOpmlServiceImpl.class);
	
	@Autowired
	protected FeedListService feedListService;

	@Autowired
	protected MasterFeedService masterFeedService;

	@Override
	public List<OpmlFeed> parseOpml(String opmlContents) {
		if (AssertUtils.isEmpty(opmlContents)) {
			return null;
		}

		return OpmlParser.parse(opmlContents);
	}

	@Override
	public List<MasterFeed> importFeeds(List<OpmlFeed> feeds) {
		FeedList feedList = this.feedListService.getOrCreate(SecurityContext.getUserID());

		List<MasterFeed> imported = new ArrayList<>();
		for (OpmlFeed feed : feeds) {
			this.importFeed(imported, feedList, feed);
		}

		// save the feed list
		this.feedListService.update(feedList);
		
		// all done
		return imported;
	}

	private void importFeed(List<MasterFeed> imported, FeedList feedList, OpmlFeed feed) {
		final boolean isFolder = AssertUtils.isNotEmpty(feed.children);

		if (isFolder) {
			UserFeedFolder folder = feedList.getOrCreateFolder(feed.title);
			this.importFeedsInFolder(imported, folder, feed.children);
			return;
		}

		if (AssertUtils.isEmpty(feed.xmlUrl)) {
			return;
		}

		LOGGER.info("Importing feed: {}", feed.xmlUrl);
		MasterFeed mf = this.masterFeedService.getOrCreateFeedForUrl(feed.title, feed.xmlUrl);
		feedList.addFeed(mf);
		imported.add(mf);
	}

	private void importFeedsInFolder(List<MasterFeed> imported, UserFeedFolder folder, List<OpmlFeed> children) {
		if (AssertUtils.isEmpty(children)) {
			return;
		}

		for (OpmlFeed feed : children) {
			if (AssertUtils.isEmpty(feed.xmlUrl)) {
				return;
			}

			LOGGER.info("Importing feed: {} in folder: {}", feed.xmlUrl, folder.title);
			MasterFeed mf = this.masterFeedService.getOrCreateFeedForUrl(feed.title, feed.xmlUrl);
			folder.addFeed(mf);
			imported.add(mf);
		}
	}

}
