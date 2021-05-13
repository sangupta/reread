package com.sangupta.reread.core;

/**
 * An entity that stores creation time stamp.
 * 
 * @author sangupta
 *
 */
public interface CreationTimeStampedEntity {
	
    public long getCreated();

    public void setCreated(long creationTime);

}
