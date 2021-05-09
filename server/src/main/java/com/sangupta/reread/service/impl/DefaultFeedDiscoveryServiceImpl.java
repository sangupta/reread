package com.sangupta.reread.service.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.constants.HttpHeaderName;
import com.sangupta.jerry.constants.HttpMimeType;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.UriUtils;
import com.sangupta.reread.entity.DiscoveredFeed;
import com.sangupta.reread.service.FeedDiscoveryService;

@Service
public class DefaultFeedDiscoveryServiceImpl implements FeedDiscoveryService {

	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeedDiscoveryServiceImpl.class);

	@Autowired
	protected HttpService httpService;

	@Override
	public Set<DiscoveredFeed> discoverFeeds(String url) {
		final String host = UriUtils.extractHost(url);

		WebResponse response = this.httpService.getResponse(url);
		if (response == null) {
			return null;
		}

		// check if this is an HTML file or an RSS file
		final String contentType = response.getHeaders().get(HttpHeaderName.CONTENT_TYPE);
		final String content = response.getContent();

		LOGGER.debug("Content type is {}", contentType);

		if (AssertUtils.isEmpty(contentType)) {
			return null;
		}

		if (contentType.contains(HttpMimeType.RSS)) {
			return Set.of(new DiscoveredFeed(url, host, "rss"));
		}

		if (contentType.contains(HttpMimeType.RDF)) {
			return Set.of(new DiscoveredFeed(url, host, "rdf"));
		}

		if (contentType.contains(HttpMimeType.ATOM)) {
			return Set.of(new DiscoveredFeed(url, host, "atom"));
		}

		if (contentType.contains(HttpMimeType.XML)) {
			return Set.of(new DiscoveredFeed(url, host, "xml"));
		}

		if (contentType.contains(HttpMimeType.HTML)) {
			// check for presence of <?xml version="1.0" ?>
			int index = content.indexOf("<?xml version=\"");
			if (index >= 0) {
				if (index < 4) {
					return Set.of(new DiscoveredFeed(url, host, "xml"));
				}

				// check if this content is XML
				// check if we have rss content
				index = content.indexOf("<rss version=\"2.0\">");
				if (index < 40) {
					return Set.of(new DiscoveredFeed(url, host, "xml"));
				}
			}
		}
		
		return null;
	}

}
