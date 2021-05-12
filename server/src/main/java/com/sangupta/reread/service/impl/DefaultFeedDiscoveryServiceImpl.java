package com.sangupta.reread.service.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.UriUtils;
import com.sangupta.reread.discover.BlogspotDiscoveryHandler;
import com.sangupta.reread.discover.FeedDiscoveryHandler;
import com.sangupta.reread.discover.FeedlyBasedDiscoveryHandler;
import com.sangupta.reread.discover.HtmlFeedDiscoveryHandler;
import com.sangupta.reread.entity.DiscoveredFeed;
import com.sangupta.reread.service.FeedDiscoveryService;

@Service
public class DefaultFeedDiscoveryServiceImpl implements FeedDiscoveryService {

	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeedDiscoveryServiceImpl.class);

	protected static final List<FeedDiscoveryHandler> HANDLERS = List.of(
			// use feedly as the default handler for it provides us the icon/title/website
//			new FeedlyBasedDiscoveryHandler(),

			// use non-HTTP based handlers next
			new BlogspotDiscoveryHandler(),

			// last is our own discovery handler
			new HtmlFeedDiscoveryHandler());

	@Autowired
	protected HttpService httpService;

	@Override
	public Set<DiscoveredFeed> discoverFeeds(String url) {
		String host = UriUtils.extractHost(url);
		String path = UriUtils.extractPath(url);
		Set<DiscoveredFeed> feeds = null;

		for (FeedDiscoveryHandler handler : HANDLERS) {
			handler.setHttpService(this.httpService);

			if (handler.canHandleDiscovery(url, host, path)) {
				LOGGER.debug("Handler {} matched for url {}", handler.getClass().getName(), url);
				feeds = handler.discoverFeed(url, host, path);

				if (AssertUtils.isNotEmpty(feeds)) {
					return feeds;
				}
			}
		}

		return null;
	}

}
