package com.sangupta.reread.discover;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangupta.jerry.constants.HttpHeaderName;
import com.sangupta.jerry.constants.HttpMimeType;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.DiscoveredFeed;

public class HtmlFeedDiscoveryHandler implements FeedDiscoveryHandler {

	public static final Logger LOGGER = LoggerFactory.getLogger(HtmlFeedDiscoveryHandler.class);

	protected HttpService httpService;

	@Override
	public boolean canHandleDiscovery(String url, String host, String path) {
		return true;
	}

	@Override
	public Set<DiscoveredFeed> discoverFeed(String url, String host, String path) {
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

		// this should be an HTML file
		// parse it up
		return parseExpectingHtml(contentType, content, response.getCharSet(), url);
	}

	protected Set<DiscoveredFeed> parseExpectingHtml(String contentType, String content, Charset charSet, String url) {
		if (AssertUtils.isEmpty(content)) {
			return null;
		}

		if (AssertUtils.isEmpty(contentType)) {
			contentType = decipherContentTypeFromContent(content);
		}

		int index = contentType.indexOf(';');
		if (index > 0) {
			contentType = contentType.substring(0, index);
		}

		if ("text/html".equalsIgnoreCase(contentType)) {
			return parseHTML(url, content);
		}

		if ("text/xml".equalsIgnoreCase(contentType) || "application/xml".equalsIgnoreCase(contentType)) {
			if (charSet != null) {
				return parseXML(url, content.getBytes(charSet));
			}

			return parseXML(url, content.getBytes());
		}

		LOGGER.debug("Unable to identify the content type: {}", contentType);
		return null;
	}

	protected Set<DiscoveredFeed> parseXML(String url, byte[] bytes) {
		String type = isAtomOrRSSFeed(bytes);
		if (type == null) {
			return null;
		}

		return Set.of(new DiscoveredFeed(url, "", type));
	}

	protected Set<DiscoveredFeed> parseHTML(final String url, final String content) {
		Document doc = Jsoup.parse(content, url);
		if (doc == null) {
			LOGGER.error("Unable to parse HTML via JSOUP from URL: {}", url);
			return null;
		}

		final Set<DiscoveredFeed> feeds = new HashSet<>();

		// find links in header code
		findHTMLAlternateLinks(url, doc, feeds);

		return feeds;
	}

	protected void findHTMLAlternateLinks(final String url, Document doc, final Set<DiscoveredFeed> feeds) {
		Elements elements = doc.getElementsByTag("link");
		if (elements == null || elements.size() == 0) {
			// no direct link tag found
			return;
		}

		for (int index = 0; index < elements.size(); index++) {
			Element element = elements.get(index);
			if ("alternate".equalsIgnoreCase(element.attr("rel"))) {
				// we have a link with us
				String title = element.attr("title");
				String href = element.absUrl("href");
				String type = element.attr("type");

				if (AssertUtils.isEmpty(href) || AssertUtils.isEmpty(type)) {
					// skip this one
					LOGGER.debug("No href/type specified for alternate in url: {}", url);
					continue;
				}

				feeds.add(new DiscoveredFeed(href, title, type));
			}
		}
	}

	protected String decipherContentTypeFromContent(String content) {
		content = content.trim();

		if (content.indexOf("<?xml") < 10) {
			return "text/xml";
		}

		if (content.indexOf("<html") < 10) {
			return "text/html";
		}

		return null;
	}

	protected String isAtomOrRSSFeed(byte[] bytes) {
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setNamespaceAware(true);
			DocumentBuilder builder = f.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(bytes));
			org.w3c.dom.Element e = doc.getDocumentElement();
			if (e.getLocalName().equals("feed") && e.getNamespaceURI().equals("http://www.w3.org/2005/Atom")) {
				return "atom";
			}

			if (e.getLocalName().equals("rss")) {
				return "rss";
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

}
