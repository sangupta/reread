package com.sangupta.reread.service.impl;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.PostImage;
import com.sangupta.reread.service.PostSnippetService;

@Service
public class DefaultPostSnippetServiceImpl implements PostSnippetService {

	public static final int HTML_SNIPPET_TEXT_LENGTH_MIN = 100;

	public static final int HTML_SNIPPET_TEXT_LENGTH_MAX = 300;

	public static final Set<String> PERMITTED_IFRAME_DOMAINS = Set.of("youtube.com");

	@Autowired
	protected HttpService httpService;

	@Override
	public void createSnippets(Post post) {
		if (post == null) {
			return;
		}

		String content = post.content;

		Snippet snippet = extract(post.link, content, post.enclosureURL);

		setFromSnippet(post, snippet);

		// reset content if modified by snippet processor
		if (snippet.content != null) {
			content = snippet.content;
		}

		// post title - only set if not already done
		if (AssertUtils.isEmpty(post.title)) {
			post.title = snippet.postTitle;
		}

		// massage the content for delay loading of all images etc
		// this will make sure that we load content really fast
		String baseURL = post.baseUrl;
		if (AssertUtils.isEmpty(baseURL)) {
			baseURL = post.link;
		}

		content = massageContent(content, baseURL);
		post.content = content;
		
		if(AssertUtils.isEmpty(post.title) && AssertUtils.isNotEmpty(post.snippet)) {
			int len = post.snippet.length();
			if(len <= 50) {
				post.title = post.snippet;
			} else {
				// TODO: add ellipsis at the break of word
				post.title = post.snippet.substring(0, 40) + "...";
			}
		}
	}

	protected Snippet extract(final String feedURL, final String content, final String enclosedImage) {
		if (AssertUtils.isEmpty(content)) {
			return new Snippet();
		}

		// parse to find snippet text
		final Document document = Jsoup.parse(content);

		Snippet snippet = new Snippet();

		// remove all objects
		document.select("object").remove();

		// extract the snippet text
		String allText = document.body().text();
		snippet.allText = allText;
		snippet.snippetText = getPlainText(allText);

		return snippet;
	}

	private static String getPlainText(String text) {
		// if text is smaller, return as is
		if (text.length() <= HTML_SNIPPET_TEXT_LENGTH_MIN) {
			return text;
		}

		// text is larger... we must trim it down
		int minIndex = Integer.MAX_VALUE;
		final int exclamation = text.indexOf('!', HTML_SNIPPET_TEXT_LENGTH_MIN);
		if (exclamation != -1) {
			minIndex = Math.min(exclamation, minIndex);
		}

		final int question = text.indexOf('?', HTML_SNIPPET_TEXT_LENGTH_MIN);
		if (question != -1) {
			minIndex = Math.min(question, minIndex);
		}

		final int dot = text.indexOf(". ", HTML_SNIPPET_TEXT_LENGTH_MIN);
		if (dot != -1) {
			minIndex = Math.min(dot, minIndex);
		}

		if (minIndex != -1) {
			if (minIndex < text.length()) {
				// not using new String() may cause memory leaks
				return new String(text.substring(0, minIndex + 1));
			}
		}

		// check for overflow
		while (text.length() > HTML_SNIPPET_TEXT_LENGTH_MAX) {
			int minEndIndex = Integer.MAX_VALUE;

			// we need to shorten this text
			final int endExclamation = text.lastIndexOf('!');
			if (endExclamation != -1) {
				minEndIndex = Math.min(endExclamation, minEndIndex);
			}

			final int endQuestion = text.lastIndexOf('?');
			if (endQuestion != -1) {
				minEndIndex = Math.min(endQuestion, minEndIndex);
			}

			final int endDot = text.lastIndexOf(". ");
			if (endDot != -1) {
				minEndIndex = Math.min(endDot, minEndIndex);
			}

			if (minEndIndex < text.length()) {
				// not using new String() may cause memory leaks
				text = new String(text.substring(0, minEndIndex + 1));
			} else {
				// not using new String() may cause memory leaks
				text = new String(text.substring(0, HTML_SNIPPET_TEXT_LENGTH_MAX));
				break;
			}
		}

		return text;
	}

	protected String massageContent(String content, final String baseUrl) {
		if (AssertUtils.isEmpty(content)) {
			return content;
		}

		Document document = Jsoup.parse(content, baseUrl);

		// remove all scripts
		document.getElementsByTag("script").remove();
		
		// convert urls to absolute
		convertToAbsoluteURI(document, "a", "href");
		convertToAbsoluteURI(document, "img", "src");

		return document.body().html();
	}

	protected void convertToAbsoluteURI(final Document document, final String tagName, final String attributeName) {
		Elements elements = document.select(tagName);
		if (elements == null || elements.isEmpty()) {
			return;
		}

		for (int index = 0; index < elements.size(); index++) {
			Element element = elements.get(index);
			final String href = element.attr(attributeName);
			if (AssertUtils.isEmpty(href)) {
				continue;
			}

			// skip all anchor hooks
			if ("#".equals(href)) {
				continue;
			}

			// skip all javascript calls
			if (href.startsWith("javascript:")) {
				continue;
			}

			element.attr(attributeName, element.absUrl(attributeName));
		}
	}

	protected void setFromSnippet(Post entry, Snippet snippet) {
		if (snippet == null) {
			entry.snippet = null;
			entry.image = null;
			entry.mainElement = null;
			entry.thumbnail = null;
			return;
		}

		entry.plainText = snippet.allText;
		entry.snippet = snippet.snippetText;
		entry.image = snippet.articleImage;
		entry.mainElement = snippet.articleElement;
		entry.thumbnail = snippet.thumb;
	}

	private static class Snippet {
		
		public String allText;

		public String snippetText;

		public PostImage articleImage;

		public PostImage thumb;

		public String articleElement;

		public String content;

		public String postTitle;

	}
}
