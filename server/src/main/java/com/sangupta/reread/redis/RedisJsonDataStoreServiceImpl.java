package com.sangupta.reread.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.Path;
import com.sangupta.jerry.exceptions.NotImplementedException;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.core.DataStoreService;

/**
 * A Redis {@link JReJSON} based implementation to the {@link DataStoreService}.
 * 
 * @author sangupta
 *
 * @param <T>
 */
public class RedisJsonDataStoreServiceImpl<T> extends AbstractDataStoreServiceImpl<T> implements DataStoreService<T> {
	
    @Autowired
    protected JReJSON jsonClient;
    
    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

	@Override
	public T get(String primaryID) {
		if(primaryID == null) {
			return null;
		}
		
		String prefix = this.getKeyPrefix();
		if(!primaryID.startsWith(prefix)) {
			primaryID = prefix + primaryID;
		}
		
		try {
			return this.jsonClient.get(primaryID, this.getEntityClass(), new Path("."));
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public List<T> getAll() {
		List<T> list = new ArrayList<>();
		Set<String> set = this.redisTemplate.keys(this.getKeyPrefix() + "*");
		if(AssertUtils.isEmpty(set)) {
			return list;
		}
		
		for(String key : set) {
			T entity = this.get(key);
			if(entity != null) {
				list.add(entity);
			}
		}
		
		return list;
	}

	@Override
	public List<T> getAll(int page, int pageSize) {
		throw new NotImplementedException();
	}

	@Override
	public boolean insert(T entity) {
		String primaryID = this.getPrimaryID(entity);
		if(primaryID != null) {
			throw new IllegalArgumentException("Entity already exists in the datastore");
		}
		
		primaryID = this.generateID();
		this.setPrimaryID(entity, primaryID);
		this.massage(entity, false);
		
		this.jsonClient.set(this.getKeyPrefix() + primaryID, entity);
		return true;
	}

	@Override
	public boolean update(T entity) {
		String primaryID = this.getPrimaryID(entity);
		if(primaryID == null) {
			throw new IllegalArgumentException("Cannot update an entity with null primary ID");
		}
		
		this.massage(entity, true);
		
		this.jsonClient.set(this.getKeyPrefix() + primaryID, entity);
		return true;
	}

	@Override
	public boolean upsert(T entity) {
		String primaryID = this.getPrimaryID(entity);
		boolean updateRequest = true;
		if(primaryID == null) {
			primaryID = this.generateID();
			updateRequest = false;
		}
		
		this.massage(entity, updateRequest);
		
		this.jsonClient.set(this.getKeyPrefix() + primaryID, entity);
		return true;
	}

	@Override
	public T delete(String primaryID) {
		primaryID = this.getKeyPrefix() + primaryID;
		T entity = this.get(primaryID);
		if(entity == null) {
			return null;
		}
		
		this.redisTemplate.delete(primaryID);
		return entity;
	}

	

	@Override
	public List<T> getAllForUser(String userID) {
		throw new NotImplementedException("Not yet implemented");
	}

	@Override
	public long count() {
		Set<String> set = this.redisTemplate.keys(this.getKeyPrefix() + "*");
		if(set == null) {
			return 0;
		}
		
		return set.size();
	}

	@Override
	public void deleteAll() {
		this.redisTemplate.delete(this.getKeyPrefix() + "*");
	}

	@Override
	public boolean updateField(T entity, String field, Object value) {
		String primaryID = this.getPrimaryID(entity);
		if(primaryID == null) {
			return false;
		}
		
		this.jsonClient.set(this.getKeyPrefix() + primaryID, value, new Path("." + field));
		return true;
	}

}
