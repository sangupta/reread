package com.sangupta.reread.entity;

import org.springframework.data.annotation.Id;

import com.sangupta.jerry.util.UriUtils;
import com.sangupta.jerry.util.UrlCanonicalizer;

public class MasterFeed {

	@Id
	public String feedID;

	public final String url;

	public String siteUrl;

	public final String normalizedUrl;

	public String title;

	public MasterFeed(String url) {
		url = UrlCanonicalizer.canonicalize(url);
		this.url = url;
		this.normalizedUrl = normalizeUrl(url);
	}

	public static String getNormalizedUrl(String url) {
		return normalizeUrl(UrlCanonicalizer.canonicalize(url));
	}
	
	private static String normalizeUrl(String url) {
		return UriUtils.extractHost(url) + "/" + UriUtils.removeSchemeAndDomain(url);
	}

	@Override
	public String toString() {
		return "[ MasterFeed: " + this.feedID + "; url: " + this.url + "]";
	}
}
