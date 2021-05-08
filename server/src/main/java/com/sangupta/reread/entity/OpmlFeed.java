package com.sangupta.reread.entity;

import java.util.ArrayList;
import java.util.List;

public class OpmlFeed {
	
public String text;
	
	public String title;
	
	public String type;
	
	public String xmlUrl;
	
	public String htmlUrl;
	
	public final List<OpmlFeed> children = new ArrayList<>();
	
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

}
