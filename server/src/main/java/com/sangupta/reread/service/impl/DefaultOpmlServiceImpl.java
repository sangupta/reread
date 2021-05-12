package com.sangupta.reread.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.DiscoveredFeed;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.OpmlFeed;
import com.sangupta.reread.entity.UserFeedFolder;
import com.sangupta.reread.service.FeedDiscoveryService;
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

	@Autowired
	protected FeedDiscoveryService feedDiscoveryService;

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

		final List<MasterFeed> masterFeeds = this.masterFeedService.getAll();
		List<MasterFeed> imported = new ArrayList<>();
		for (OpmlFeed feed : feeds) {
			this.importFeed(masterFeeds, imported, feedList, feed);
		}

		// save the feed list
		this.feedListService.update(feedList);

		// all done
		return imported;
	}

	private void importFeed(List<MasterFeed> masterFeeds, List<MasterFeed> feedsImported, FeedList feedList,
			OpmlFeed opmlFeed) {
		final boolean isFolder = AssertUtils.isNotEmpty(opmlFeed.children);

		if (isFolder) {
			UserFeedFolder folder = feedList.getOrCreateFolder(opmlFeed.title);
			this.importFeedsInFolder(masterFeeds, feedsImported, folder, opmlFeed.children);
			return;
		}

		if (AssertUtils.isEmpty(opmlFeed.xmlUrl)) {
			return;
		}

		LOGGER.info("Importing feed: {}", opmlFeed.xmlUrl);
		MasterFeed masterFeed = this.getMasterFeed(masterFeeds, feedsImported, opmlFeed);
		feedList.addFeed(masterFeed);
	}

	private void importFeedsInFolder(List<MasterFeed> masterFeeds, List<MasterFeed> feedsImported,
			UserFeedFolder folder, List<OpmlFeed> children) {
		if (AssertUtils.isEmpty(children)) {
			return;
		}

		for (OpmlFeed opmlFeed : children) {
			if (AssertUtils.isEmpty(opmlFeed.xmlUrl)) {
				return;
			}

			LOGGER.info("Importing feed: {} in folder: {}", opmlFeed.xmlUrl, folder.title);
			MasterFeed masterFeed = this.getMasterFeed(masterFeeds, feedsImported, opmlFeed);
			folder.addFeed(masterFeed);
		}
	}

	private MasterFeed getMasterFeed(List<MasterFeed> masterFeeds, List<MasterFeed> feedsImported, OpmlFeed feed) {
		MasterFeed masterFeed = null;

		Set<DiscoveredFeed> set = this.feedDiscoveryService.discoverFeeds(feed.xmlUrl);
		if (AssertUtils.isEmpty(set)) {
			masterFeed = this.masterFeedService.getOrCreateFeedForUrl(masterFeeds, feed.title, feed.xmlUrl);
		} else {
			// read first feed
			DiscoveredFeed df = set.iterator().next();
			MasterFeed mf = new MasterFeed(df);
			masterFeed = this.masterFeedService.getOrCreateFeed(masterFeeds, mf);
		}
		
		
		feedsImported.add(masterFeed);
		return masterFeed;
	}

}
