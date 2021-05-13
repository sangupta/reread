package com.sangupta.reread.core;

/**
 * An entity that stores update time stamp.
 * 
 * @author sangupta
 *
 */
public interface UpdateTimeStampedEntity {

	public long getUpdated();

    public void setUpdated(long updatedTime);
    
}
