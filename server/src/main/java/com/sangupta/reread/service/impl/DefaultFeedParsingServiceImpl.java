package com.sangupta.reread.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.constants.HttpHeaderName;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.service.FeedParsingService;
import com.sangupta.reread.utils.FeedParser;
import com.sangupta.reread.utils.WebResponseParser;

/**
 * Default implementation of the {@link FeedParsingService}.
 * 
 * @author sangupta
 *
 */
@Service
public class DefaultFeedParsingServiceImpl implements FeedParsingService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeedParsingServiceImpl.class);
	
	@Autowired
	protected HttpService httpService;

	@Override
	public ParsedFeed parseFeedFromUrl(String feedID, String url, String latestPostID) {
		final long crawlTime = System.currentTimeMillis();
		
		WebResponse response = this.httpService.getResponse(url);
		if(response == null || !response.isSuccess()) {
			return null;
		}
		
		String contents = WebResponseParser.getContentWithProperEncoding(response);
		String lastModified = response.getHeaders().get(HttpHeaderName.LAST_MODIFIED);
		String etag = response.getHeaders().get(HttpHeaderName.ETAG);
		
		ParsedFeed feed = null;
		try {
			feed = FeedParser.parse(feedID, contents, latestPostID);
		} catch(RuntimeException e) {
			LOGGER.error("Reading and parsing feed caused an error for feed with url: " + url + " and id: " + feedID, e);
			return null;
		}
		
		if(feed == null) {
			LOGGER.warn("Feed returned is null for url: {}", url);
			return null;
		}
		
		feed.crawlTime = crawlTime;
		feed.lastModifiedTimestamp = response.getLastModified();
		feed.eTagHeader = etag;
		feed.lastModifiedHeader = lastModified;
		
		// sort posts
		feed.sortPosts();
		
		// we are done
		LOGGER.info("Found {} posts in feed for url: {}", feed.posts.size(), url);
		return feed;
	}

}
