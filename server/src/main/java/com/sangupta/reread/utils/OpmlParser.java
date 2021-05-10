package com.sangupta.reread.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.OpmlFeed;

public class OpmlParser {
	
	public static List<OpmlFeed> parse(String opml) {
		if (AssertUtils.isEmpty(opml)) {
			return null;
		}

		Document doc = Jsoup.parse(opml);
		return parse(doc);
	}

	/**
	 * Parse the Jsoup HTML source for feeds
	 * 
	 * @param source
	 * @return
	 */
	protected static List<OpmlFeed> parse(Document source) {
		if (source == null) {
			return null;
		}

		List<OpmlFeed> feeds = new ArrayList<>();

		Elements elements = source.select("outline");
		if (elements == null || elements.isEmpty()) {
			return null;
		}

		parseElements(elements, feeds);
		
		return feeds;
	}
	
	protected static void parseElements(Elements elements, List<OpmlFeed> list) {
		for (Element element : elements) {
			OpmlFeed feed = new OpmlFeed();
			feed.title = element.attr("title");
			feed.text = element.attr("text");
			feed.type = element.attr("type");
			feed.xmlUrl = element.attr("xmlUrl");
			feed.htmlUrl = element.attr("htmlUrl");
			
			list.add(feed);

			Elements children = element.children().select("outline");
			if (children == null || children.isEmpty()) {
				continue;
			}
			
			parseElements(children, feed.children);
		}
	}
	
}
