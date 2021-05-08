package com.sangupta.reread.core;

import java.util.Collection;
import java.util.List;

/**
 * Borrowed from https://github.com/sangupta/jerry-services
 * 
 * @author sangupta
 * 
 * @param <T> The type of entity object which the data store works with
 * 
 * @param <X> The type of primary key for this entity object
 */
public interface DataStoreService<T> {
    
    /**
     * Gets the entity class that this data store handles.
     * 
     * @return The entity class handled by this store.
     */
    public Class<T> getEntityClass();
    
    /**
     * Given an entity return the primary ID associated with it.
     * 
     * @param entity the entity instance for which primary key is required
     * 
     * @return the primary key
     */
    public String getPrimaryID(T entity);

    /**
     * Retrieve the entity object with the given primary key.
     * 
     * @param primaryID the primary key for which to look for the object
     * 
     * @return the object that is stored for the given primary key
     */
    public T get(String primaryID);

    /**
     * Retrieves a list of all entities in the datastore that match the list of
     * given primary identifiers.
     * 
     * <b>Note:</b> If there are too many entity identifiers supplied, the code may
     * go out of memory or may take too long to complete. This method should be used
     * only by developer at discretion.
     * 
     * @param ids the primary key identifiers for which we need to fetch the
     *            objects.
     * 
     * @return {@link List} of entity objects as fetched for the given identifiers.
     * 
     */
    public List<T> getMultiple(Collection<String> ids);

    /**
     * Retrieves a list of all entities in the datastore that match the list of
     * given primary identifiers.
     * 
     * <b>Note:</b> If there are too many entity identifiers supplied, the code may
     * go out of memory or may take too long to complete. This method should be used
     * only by developer at discretion.
     * 
     * @param ids an array of primary key identifiers for which we need to fetch the
     *            objects.
     * 
     * @return {@link List} of entity objects as fetched for the given identifiers.
     */
    public List<T> getMultiple(String[] ids);

    /**
     * Retrieves a list of all entities in the datastore.
     * 
     * <b>Note:</b> If there are too many entities in the data store, the code may
     * go out of memory or may take too long to complete. This method should be used
     * only by developers at discretion.
     * 
     * It is recommended not to use this method in production instances. Rather, use
     * the method {@link #getAll(int, int)}.
     * 
     * @return all the objects in the data store
     * 
     */
    public List<T> getAll();

    /**
     * Retrieves a list of entities for the given page number with the give page
     * size. The page numbering starts from 1.
     * 
     * @param page     the page for which the results are needed, 1-based
     * 
     * @param pageSize the page size to use
     * 
     * @return the list of all objects falling in that page
     * 
     */
    public List<T> getAll(int page, int pageSize);
    
    /**
     * Retrieves a list of all entities in the data store that are owned by the
     * user. If the persisting entity is not a type of {@link UserOwnedEntity} then
     * this method throws {@link UnsupportedOperationException}.
     * 
     * @param userID the user for whom the entities are desired
     * 
     * @return a {@link List} of all entities in the store.
     */
    public List<T> getAllForUser(String userID);

    /**
     * Insert a new entity object into the data store
     * 
     * @param entity the entity that needs to be saved
     * 
     * @return <code>true</code> if the value was saved, <code>false</code>
     *         otherwise.
     * 
     */
    public boolean insert(T entity);

    /**
     * Update the entity object into the data store
     * 
     * @param entity the entity to be updated in the data store
     * 
     * @return <code>true</code> if the entity was updated, <code>false</code>
     *         otherwise.
     * 
     */
    public boolean update(T entity);

    /**
     * Add or update the entity object into the data store
     * 
     * @param entity the object that needs to be persisted or updated in the data
     *               store.
     * 
     * @return <code>true</code> if the entity was saved, <code>false</code>
     *         otherwise.
     */
    public boolean upsert(T entity);

    /**
     * Delete the data store entity with the given primary key
     * 
     * @param primaryID the primary key of the object that needs to be deleted
     * 
     * @return <code>true</code> if the entity was deleted, <code>false</code>
     *         otherwise
     * 
     */
    public T delete(String primaryID);

    public List<T> deleteMultiple(Collection<String> ids);

    public List<T> deleteMultiple(String[] ids);

    /**
     * Return the count of total objects in the data store
     * 
     * @return the number of objects in the database
     * 
     */
    public long count();

    /**
     * Clean the database of all entities in this collection.
     * 
     */
    public void deleteAll();
    
    public boolean updateField(T entity, String field, Object value);

}
