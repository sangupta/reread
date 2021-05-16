package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Represents one feed in the OPML file.
 * Entity is not written to DB.
 * 
 * @author sangupta
 *
 */
public class OpmlFeed {
	
	public String text;
	
	public String title;
	
	public String type;
	
	public String xmlUrl;
	
	public String htmlUrl;
	
	public String iconUrl;
	
	public final List<OpmlFeed> children = new ArrayList<>();
	
	public OpmlFeed() {
		// default constructor
	}
	
	public OpmlFeed(UserFeed feed, MasterFeed mf) {
		this.title = feed.title;
		this.text = feed.title;
		this.type = "rss";
		this.htmlUrl = feed.website;
		this.iconUrl = feed.iconUrl;
		this.xmlUrl = mf.url;
	}

	@Override
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		
		if(!(other instanceof OpmlFeed)) {
			return false;
		}
		
		OpmlFeed feed = (OpmlFeed) other;
		if(this.xmlUrl == null) {
			return false;
		}
		
		return this.xmlUrl.equals(feed.xmlUrl);
	}
	
	@Override
	public int hashCode() {
		if(this.xmlUrl == null) {
			return 0;
		}
		
		return this.xmlUrl.hashCode();
	}
	
	@Override
	public String toString() {
		return this.title + " (" + this.xmlUrl + ")";
	}

	public void writeXml(StringBuilder builder, String newLine) {
		builder.append("<outline text=\"");
		builder.append(StringEscapeUtils.escapeXml11(this.text));
		builder.append("\" title=\"");
		builder.append(StringEscapeUtils.escapeXml11(this.title));
		
		if(this.children.size() == 0) {
			builder.append("\" type=\"");
			builder.append(StringEscapeUtils.escapeXml11(this.type));
			builder.append("\" xmlUrl=\"");
			builder.append(StringEscapeUtils.escapeXml11(this.xmlUrl));
			builder.append("\" htmlUrl=\"");
			builder.append(StringEscapeUtils.escapeXml11(this.htmlUrl));
			builder.append("\" iconUrl=\"");
			builder.append(StringEscapeUtils.escapeXml11(this.iconUrl));
			builder.append("\" />");
			builder.append(newLine);
		} else {
			builder.append("\" >");
			builder.append(newLine);
			for(OpmlFeed child : this.children) {
				child.writeXml(builder, newLine);
			}
			builder.append("</outline>");
			builder.append(newLine);
		}
	}

}
