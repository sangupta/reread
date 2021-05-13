package com.sangupta.reread.entity;

import org.springframework.data.annotation.Id;

/**
 * Post constructed by parsing the feed URL contents. Entity 
 * is written to DB.
 * 
 * @author sangupta
 *
 */
public class Post implements Comparable<Post> {
	
	@Id
	public String feedPostID;
	
	public String masterFeedID;
	
	public PostAuthor author;
	
	public String title;
	
	public String baseUrl;
	
	/**
	 * HTML content of the entry
	 */
	public String content;
	
	/**
	 * Text used for indexing purpose and thus is declared transient
	 */
	public transient String plainText;
	
	public String snippet;
	
	public PostImage image;
	
	public PostImage thumbnail;
	
	public String mainElement;
	
	public String link;
	
	public long readOn;
	
	public long starredOn;
	
	public long bookmarkedOn;
	
	public long updated;
	
	public String hash;
	
	public String uniqueID;
	
	public transient String enclosureURL;
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(this == obj) {
			return true;
		}
		
		if(obj instanceof Post) {
			Post post = (Post) obj;
			if(this.link != null) {
				return this.link.equals(post.link);
			}
			
			if(this.title != null && this.content != null) {
				return this.title.equals(post.title) && this.content.equals(post.content);
			}
			
			if(this.title != null) {
				return post.content == null && this.title.equals(post.title);
			}
			
			if(this.content != null) {
				return post.title == null && this.content.equals(post.content);
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		if(this.link != null) {
			return this.link.hashCode();
		}
		
		if(this.title != null && this.content != null) {
			return this.title.hashCode() << 17 + this.content.hashCode();
		}
		
		if(this.title != null) {
			return this.title.hashCode();
		}
		
		if(this.content != null) {
			return this.content.hashCode();
		}
		
		return super.hashCode();
	}
	
	@Override
	public int compareTo(Post other) {
		if(other == null) {
			return -1;
		}
		
		if(this.updated > 0 && other.updated > 0) {
			if(this.updated > other.updated) {
				return -1;
			}
			
			return 1;
		}
		
		if(this.title != null && other.title != null) {
			return this.title.compareTo(other.title);
		}
		
		if(this.link != null && other.link != null) {
			return this.link.compareTo(other.link);
		}

		if(this.uniqueID != null && other.uniqueID != null) {
			return this.uniqueID.compareTo(other.uniqueID);
		}
		
		throw new IllegalStateException("One of the entries compared does not have updated date, title, hyperlink nor unique id");
	}
	
	@Override
	public String toString() {
		return this.title + " - " + this.feedPostID + "@" + this.masterFeedID;
	}

}
