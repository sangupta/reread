package com.sangupta.reread.redis;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

import com.sangupta.jerry.exceptions.NotImplementedException;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.GsonUtils;
import com.sangupta.reread.core.DataStoreService;

public class RedisTemplateDataStoreServiceImpl<T> extends AbstractDataStoreServiceImpl<T>  implements DataStoreService<T> {

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
			String json = this.redisTemplate.opsForValue().get(primaryID);
			return GsonUtils.getGson().fromJson(json, this.entityClass);
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
		
		String json = GsonUtils.getGson().toJson(entity);
		this.redisTemplate.opsForValue().set(this.getKeyPrefix() + primaryID, json);
		return true;
	}

	@Override
	public boolean update(T entity) {
		String primaryID = this.getPrimaryID(entity);
		if(primaryID == null) {
			throw new IllegalArgumentException("Cannot update an entity with null primary ID");
		}
		
		this.massage(entity, true);
		
		String json = GsonUtils.getGson().toJson(entity);
		this.redisTemplate.opsForValue().set(this.getKeyPrefix() + primaryID, json);
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
		
		String json = GsonUtils.getGson().toJson(entity);
		this.redisTemplate.opsForValue().set(this.getKeyPrefix() + primaryID, json);
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
	public boolean updateField(T entity, String fieldName, Object value) {
		String primaryID = this.getPrimaryID(entity);
		if(primaryID == null) {
			return false;
		}
		
		FieldFilter filter = new FieldFilter() {
			
			@Override
			public boolean matches(Field field) {
				return field.getName().equals(fieldName);
			}
			
		};
		
		T entityInStore = this.get(primaryID);
		Field field = ReflectionUtils.findField(entityClass, filter);
		
		ReflectionUtils.setField(field, entityInStore, value);
		
		String json = GsonUtils.getGson().toJson(entity);
		this.redisTemplate.opsForValue().set(this.getKeyPrefix() + primaryID, json);		
		return true;
	}
	
}
