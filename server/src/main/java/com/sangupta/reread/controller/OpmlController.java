package com.sangupta.reread.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.OpmlFeed;
import com.sangupta.reread.service.FeedRefreshService;
import com.sangupta.reread.service.OpmlService;

/**
 * REST end points related to OPML file functionality.
 * 
 * @author sangupta
 *
 */
@RestController
@RequestMapping("/opml")
public class OpmlController {

	/**
	 * My instance logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OpmlController.class);

	@Autowired
	protected OpmlService opmlService;

	@Autowired
	protected FeedRefreshService feedRefreshService;

	/**
	 * End point dedicated about importing a new OPML file.
	 * 
	 * @param bytes   OPML file represented as bytes
	 * 
	 * @param confirm <code>false</code> if we are only discovering new feeds,
	 *                <code>true</code> if we subscribing to all of these feeds.
	 * 
	 * @return
	 */
	@PostMapping("/import")
	public List<OpmlFeed> importOpml(@RequestBody byte[] bytes, @RequestParam(required = false, defaultValue = "false") boolean confirm) {
		if (AssertUtils.isEmpty(bytes)) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}

		// read file in UTF-8 encoding
		String file = new String(bytes, StandardCharsets.UTF_8);
		List<OpmlFeed> feeds = this.opmlService.parseOpml(file);
		if (!confirm) {
			return feeds;
		}

		// import all feeds
		List<MasterFeed> imported = this.opmlService.importFeeds(feeds);

		// schedule a background thread to refresh these feeds immediately
		this.feedRefreshService.refreshFeeds(imported);

		LOGGER.info("OPML import is complete with {} feeds", imported.size());

		// all done
		return feeds;
	}

}
