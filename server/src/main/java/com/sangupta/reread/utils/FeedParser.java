package com.sangupta.reread.utils;

import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.DateUtils;
import com.sangupta.jerry.util.HashUtils;
import com.sangupta.jerry.util.UriUtils;
import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.PostAuthor;
import com.sun.syndication.feed.module.DCModuleImpl;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Utility class to help parse a feed contents.
 * 
 * @author sangupta
 *
 */
public class FeedParser {

	/**
	 * My logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FeedParser.class);

	/**
	 * Valid image types
	 */
	private static final Set<String> VALID_IMAGE_TYPES = Set.of("image/png", "image/gif", "image/jpg", "image/jpeg");

	/**
	 * Parse a given feed whose contents are provided. Ignore entries older than the latest
	 * post ID provided.
	 * 
	 * @param masterFeedID the feedID represented in system
	 * 
	 * @param feedContents the feed contents in proper encoding
	 * 
	 * @param latestPostID most recent post ID that was crawled, if available
	 * 
	 * @return a {@link ParsedFeed} instance consisting of all details
	 */
	public static ParsedFeed parse(String masterFeedID, String feedContents, String latestPostID) {
		try {
			return parseInternal(masterFeedID, feedContents, latestPostID);
		} catch (RuntimeException e) {
			LOGGER.error("Unable to parse contents for feedID: " + masterFeedID, e);
			return null;
		}
	}

	protected static ParsedFeed parseInternal(String masterFeedID, String feedContents, String latestPostID) {
		SyndFeed feed = extractSyndFeed(masterFeedID, feedContents);
		if (feed == null) {
			LOGGER.error("Feed object is null for contents: {}", feedContents);
			return null;
		}

		if (AssertUtils.isEmpty(feed.getEntries())) {
			LOGGER.debug("no entry in feed");
			return null;
		}

		// start parsing the entire thing now
		final long start = System.currentTimeMillis();
		ParsedFeed parsedFeed = getParsedFeed(masterFeedID, feed, latestPostID);

		final List<Post> entries = parsedFeed.posts;
		if (AssertUtils.isNotEmpty(entries)) {
			Collections.sort(entries);
		}

		// check and add post title, if not present
		for (Post post : parsedFeed.posts) {
			if (AssertUtils.isEmpty(post.title)) {
				post.title = parsedFeed.feedTitle;
			}
		}

		final long end = System.currentTimeMillis();

		LOGGER.debug("Feed {} parsed in {}ms", masterFeedID, (end - start));

		return parsedFeed;
	}

	@SuppressWarnings("deprecation")
	protected static SyndFeed extractSyndFeed(String masterFeedID, String feedContents) {
		if (AssertUtils.isEmpty(feedContents)) {
			return null;
		}

		SyndFeed feed = null;
		StringReader reader = new StringReader(feedContents);

		// this makes sure that we can read feeds with BOM
		int prolog = feedContents.indexOf("<?xml");
		if (prolog > 0 && prolog < 3) {
			try {
				for (int index = 0; index < prolog; index++) {
					reader.read();
				}
			} catch (Exception e) {
				// eat up
			}
		}

		SyndFeedInput input = new SyndFeedInput();
		input.setXmlHealerOn(true);
		try {
			feed = input.build(reader);
		} catch (IllegalArgumentException e) {
			// this may happen if the feed is an RDF feed
			int index = feedContents.indexOf("rdf:RDF");

			if (index == -1) {
				LOGGER.error("Unable to read feed from feed contents: {}", feedContents, e);
				return null;
			}

			// the feeds is an RDF feed
			// let's add a line about the missing header
			// so as to give another shot at parsing
			// let's inject the namespace of xmlns="http://purl.org/rss/1.0/"
			StringBuilder builder = new StringBuilder(feedContents);
			builder.insert(index + 7, " xmlns=\"http://purl.org/rss/1.0/\" ");
			feedContents = builder.toString();

			// now that NS has been injected
			// try and parse again
			try {
				IOUtils.closeQuietly(reader);

				reader = new StringReader(feedContents);

				feed = input.build(reader);
			} catch (IllegalArgumentException e1) {
				LOGGER.error("Unable to read feed from feed contents: " + feedContents, e1);
			} catch (FeedException e1) {
				LOGGER.error("Unable to read feed from feed contents: {} for masterFeedID: {} due to error {}",
						feedContents, masterFeedID, e1.getMessage());
			}
		} catch (FeedException e) {
			LOGGER.error("Unable to read feed from feed contents: {} for masterFeedID: {} due to error {}",
					feedContents, masterFeedID, e.getMessage());
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return feed;
	}

	@SuppressWarnings("unchecked")
	protected static ParsedFeed getParsedFeed(final String masterFeedID, final SyndFeed feed, String latestPostID) {
		final ParsedFeed parsedFeed = new ParsedFeed();
		parsedFeed.feedTitle = feed.getTitle();
		parsedFeed.siteUrl = feed.getLink();

		List<SyndLink> links = feed.getLinks();

		parsedFeed.nextUrl = extractLink(links, "next");

		List<SyndEntry> entries = feed.getEntries();

		final String baseUrl = UriUtils.getBaseUrl(feed.getLink());

		Post post = null;
		long timeDifference = System.currentTimeMillis();
		for (SyndEntry entry : entries) {
			try {
				post = extractParsedFeedEntry(entry, baseUrl, timeDifference);
				timeDifference = timeDifference - 10;
			} catch (Exception e) {
				LOGGER.error("Unable to extract entry from feed contents", e);
			}

			// add to the list of entries
			if (post != null) {
				post.masterFeedID = masterFeedID;

				// compute has for the post
				post.hash = HashUtils.getMD5Hex(post.content);

				if(post.uniqueID != null && post.uniqueID.equalsIgnoreCase(latestPostID)) {
					// we got a unique match
					break;
				}
				
				if(latestPostID != null && latestPostID.startsWith("hash:") && post.hash.equals(latestPostID.substring(5))) {
					// we match via hash
					break;
				}
				
				// post has never been seen before - pick it up
				parsedFeed.posts.add(post);
			}
		}

		return parsedFeed;
	}

	@SuppressWarnings("unchecked")
	protected static Post extractParsedFeedEntry(SyndEntry entry, final String baseURL, final long timeDifference) {
		Post post = new Post();

		// get the author
		if (AssertUtils.isNotEmpty(entry.getAuthors())) {
			SyndPersonImpl personImpl = (SyndPersonImpl) entry.getAuthors().get(0);
			if (personImpl != null) {
				post.author = new PostAuthor();
				post.author.name = personImpl.getName();
				post.author.uri = personImpl.getUri();
			}
		}

		// extract the content
		String content = null;
		if (AssertUtils.isNotEmpty(entry.getContents())) {
			content = ((SyndContentImpl) entry.getContents().get(0)).getValue();
		} else {
			// wordpress stores content in description too
			if (entry.getDescription() != null) {
				content = entry.getDescription().getValue();
			}
		}

		// check if there is an image specified as an enclosure
		if (AssertUtils.isNotEmpty(entry.getEnclosures())) {

			List<SyndEnclosure> encls = entry.getEnclosures();
			for (SyndEnclosure encl : encls) {
				if (isImageType(encl.getType())) {
					post.enclosureURL = encl.getUrl();
					break;
				}
			}
		}

		// title of the post
		if (entry.getTitle() != null) {
			post.title = entry.getTitle().trim();
		} else {
			post.title = entry.getTitle();
		}

		post.baseUrl = baseURL;
		post.content = content;
		post.link = entry.getLink();
		post.uniqueID = entry.getUri();

		// updated date
		Date updatedDate = entry.getUpdatedDate();
		if (updatedDate == null) {
			// wordpress returns the value in modules
			// the first module being an implementation of DCModuleImpl
			if (entry.getModules() != null) {
				DCModuleImpl module = (DCModuleImpl) entry.getModules().get(0);
				if (module.getDate() != null) {
					updatedDate = module.getDate();
				}
			}
		}

		// if we still cannot find the date, let's choose the current date
		// of crawl - atleast we will have something to display
		if (updatedDate != null) {
			// check if we are in future
			// and that too offset by a large amount
			if (updatedDate.getTime() - System.currentTimeMillis() > DateUtils.FIFTEEN_MINUTES) {
				updatedDate = null;
			}
		}

		if (updatedDate == null) {
			// the time difference of roughly 10 millis is added
			updatedDate = new Date(timeDifference);
		}

		post.updated = updatedDate.getTime();

		return post;
	}

	/**
	 * Is given image type a valid image type.
	 * 
	 * @param type
	 * @return
	 */
	protected static boolean isImageType(String type) {
		return VALID_IMAGE_TYPES.contains(type);
	}

	protected static String extractLink(List<SyndLink> links, String linkRel) {
		if (links == null || links.isEmpty()) {
			return null;
		}

		for (SyndLink link : links) {
			if (linkRel.equalsIgnoreCase(link.getRel())) {
				return link.getHref();
			}
		}

		return null;
	}

}
