package com.sangupta.reread.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.imagelib.ImageLibDimensions;
import com.sangupta.imagelib.vo.ImageDimensions;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.PostImage;
import com.sangupta.reread.service.PostSnippetService;

/**
 * Default implementation for the {@link PostSnippetService}.
 * 
 * @author sangupta
 *
 */
@Service
public class DefaultPostSnippetServiceImpl implements PostSnippetService {

	public static final int MIN_IMAGE_DIMENSION_HEIGHT = 20;

	public static final int MIN_IMAGE_DIMENSION_WIDTH = 20;

	public static final List<String> BLOCKED_IMAGE_URLs = new ArrayList<>();

	public static final List<String> REMOVABLE_IMAGE_URLs = new ArrayList<>();

	static {
		// blocked
		BLOCKED_IMAGE_URLs.add("https://fi1.ypncdn.com/");

		// removable
		REMOVABLE_IMAGE_URLs.add("http://feeds.feedburner.com/~ff");
		REMOVABLE_IMAGE_URLs.add("https://feeds.feedburner.com/~ff");
	}

	public static final int HTML_SNIPPET_TEXT_LENGTH_MIN = 100;

	public static final int HTML_SNIPPET_TEXT_LENGTH_MAX = 300;

	public static final Set<String> PERMITTED_IFRAME_DOMAINS = Set.of("youtube.com");

	@Autowired
	protected HttpService httpService;

	@Override
	public void createSnippet(String siteLinkInFeed, Post post) {
		if (post == null) {
			return;
		}

		String content = post.content;
		if(AssertUtils.isEmpty(content)) {
			return;
		}

		// parse to find snippet text
		String urlToUseInParsing = siteLinkInFeed;
		if(AssertUtils.isEmpty(siteLinkInFeed)) {
			urlToUseInParsing = post.link; 
		}

		// massage the content for delay loading of all images etc
		String baseURL = post.baseUrl;
		if (AssertUtils.isEmpty(baseURL)) {
			baseURL = post.link;
		}

		// parse 
		final Document document = Jsoup.parse(content, urlToUseInParsing);
		Snippet snippet = generateSnippet(document, post.enclosureURL);
		
		post.image = snippet.articleImage;
		post.mainElement = snippet.articleElement;
		post.plainText = snippet.allText;
		post.snippet = snippet.snippetText;
		
		// extra sinppet
		content = massageContent(document, baseURL);
		post.content = content;

		// reset content if modified by snippet processor
		if (snippet.content != null) {
			content = snippet.content;
		}

		// post title - only set if not already done
		if (AssertUtils.isEmpty(post.title)) {
			post.title = snippet.postTitle;
		}

		if (AssertUtils.isEmpty(post.title) && AssertUtils.isNotEmpty(post.snippet)) {
			int len = post.snippet.length();
			if (len <= 50) {
				post.title = post.snippet;
			} else {
				// TODO: add ellipsis at the break of word
				post.title = post.snippet.substring(0, 40) + "...";
			}
		}
	}

	protected Snippet generateSnippet(final Document document, final String enclosedImage) {
		final Snippet snippet = new Snippet();

		// remove all objects
		document.select("object").remove();

		// find major image
		snippet.articleImage = this.extractImage(document, enclosedImage);

		// extract the snippet text
		// this needs to be the last item in this method
		// as we remove images and other clutter from the feed
		String allText = document.body().text();
		snippet.allText = allText;
		snippet.snippetText = getPlainText(allText);

		return snippet;
	}

	protected PostImage extractImage(Document document, String enclosedImage) {
		PostImage image = null;

		if (enclosedImage != null) {
			image = this.getPostImageWithDimensions(enclosedImage);
		}

		if (image != null) {
			return image;
		}

		return this.extractImageFromHtmlDocument(document);
	}

	private PostImage extractImageFromHtmlDocument(Document document) {
		PostImage postImage = null;
		
		// loop on all images
		Elements images = document.getElementsByTag("img");
		if (images != null && images.size() > 0) {
			int size = images.size();
			for (int index = 0; index < size; index++) {
				Element image = images.get(index);
				String imageUrl = image.absUrl("src");

				// check for blocked urls
				if (isBlockedUrl(imageUrl)) {
					continue;
				}

				if (isRemovableImage(imageUrl)) {
					image.remove();
					continue;
				}

				// extract height and width
				String height = image.attr("height");
				String width = image.attr("width");

				if(postImage == null) {
					// the reason we do not break here
					// is to ensure that we can remove all removable images
					postImage = this.filterImage(imageUrl, width, height);
				}
			}
		}

		return postImage;
	}

	private boolean isRemovableImage(String imageUrl) {
		for(String url : REMOVABLE_IMAGE_URLs) {
			if(imageUrl.startsWith(url)) {
				return true;
			}
		}
		
		return false;
	}

	protected PostImage filterImage(String url, String width, String height) {

		int widthValue = StringUtils.getIntValue(width, 0);
		int heightValue = StringUtils.getIntValue(height, 0);

		if (heightValue >= MIN_IMAGE_DIMENSION_HEIGHT && widthValue >= MIN_IMAGE_DIMENSION_WIDTH) {
			return new PostImage(url, widthValue, heightValue);
		}

		// send this over to the thumbnail server
		PostImage image = this.getPostImageWithDimensions(url);
		if (image != null) {
			if (image.height >= MIN_IMAGE_DIMENSION_HEIGHT && image.width >= MIN_IMAGE_DIMENSION_WIDTH) {
				return image;
			}
		}

		return null;
	}

	protected static String getPlainText(String text) {
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

	protected String massageContent(Document document, final String baseUrl) {
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

	public static boolean isBlockedUrl(String url) {
		for (String blockedURL : BLOCKED_IMAGE_URLs) {
			if (url.startsWith(blockedURL)) {
				return true;
			}
		}

		return false;
	}

	protected PostImage getPostImageWithDimensions(String url) {
		WebResponse response = httpService.getResponse(url);
		if (response == null || !response.isSuccess()) {
			return null;
		}

		byte[] bytes = response.asBytes();
		if (AssertUtils.isEmpty(bytes)) {
			return null;
		}

		ImageDimensions dim = ImageLibDimensions.getImageDimensions(bytes);
		if (dim == null) {
			return null;
		}

		return new PostImage(url, dim.width, dim.height);
	}

	private static class Snippet {

		public String allText;

		public String snippetText;

		public PostImage articleImage;

		public String articleElement;

		public String content;

		public String postTitle;

	}
}
