package com.sangupta.reread.redis;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ReflectionUtils;
import com.sangupta.reread.core.CreationTimeStampedEntity;
import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.core.UpdateTimeStampedEntity;
import com.sangupta.reread.core.UserOwnedEntity;

public abstract class AbstractDataStoreServiceImpl<T> implements DataStoreService<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataStoreServiceImpl.class);

    protected final Class<T> entityClass;

    protected final Field primaryKeyField;

    protected final String idKeyFieldName;
    
	@SuppressWarnings("unchecked")
	public AbstractDataStoreServiceImpl() {
		 // extract entity class and primary key class
        Type t;
        Class<?> tc = getClass();
        do {
            t = tc.getGenericSuperclass();
            if (t instanceof ParameterizedType) {
                break;
            }
            
            if(!(t instanceof Class)) {
                break;
            }
            tc = (Class<?>) t;
        } while(true);

        Type[] actualTypeArguments = ((ParameterizedType) t).getActualTypeArguments();
        this.entityClass = (Class<T>) actualTypeArguments[0];

        // figure out the primary field
        List<Field> fields = ReflectionUtils.getAllFields(this.entityClass);
        if (AssertUtils.isEmpty(fields)) {
            LOGGER.warn("Entity class has no field defined: {}", this.entityClass);
            throw new IllegalArgumentException("Entity class has no field defined");
        }

        // find the primary key field
        Field primaryField = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryField = field;
                break;
            }
        }

        if (primaryField == null) {
            this.primaryKeyField = null;
            this.idKeyFieldName = null;
            return;
        }

        this.primaryKeyField = primaryField;
        this.idKeyFieldName = primaryField.getName();
	}
	
    public String getPrimaryID(T entity) {
        if (this.primaryKeyField == null) {
            throw new IllegalStateException("This DataStore must either implement getPrimaryID() method, or add an @Id annotation to a field in the entity");
        }
        
        try {
            this.primaryKeyField.setAccessible(true);
            return String.class.cast(this.primaryKeyField.get(entity));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to access primary key field", e);
        }
    }

	@Override
	public Class<T> getEntityClass() {
		return this.entityClass;
	}
	
	@Override
	public List<T> getMultiple(Collection<String> ids) {
		List<T> list = new ArrayList<>();
		for(String id : ids) {
			T entity = this.get(id);
			if(entity != null) {
				list.add(entity);
			}
		}
		
		return list;
	}

	@Override
	public List<T> getMultiple(String[] ids) {
		List<T> list = new ArrayList<>();
		for(String id : ids) {
			T entity = this.get(id);
			if(entity != null) {
				list.add(entity);
			}
		}
		
		return list;
	}
	
	@Override
	public List<T> deleteMultiple(Collection<String> ids) {
		List<T> list = new ArrayList<>();
		for(String id : ids) {
			T entity = this.delete(id);
			if(entity != null) {
				list.add(entity);
			}
		}
		
		return list;
	}

	@Override
	public List<T> deleteMultiple(String[] ids) {
		List<T> list = new ArrayList<>();
		for(String id : ids) {
			T entity = this.delete(id);
			if(entity != null) {
				list.add(entity);
			}
		}
		
		return list;
	}
	
	public String getKeyPrefix() {
		return this.entityClass.getCanonicalName() + "::";
	}

	public String generateID() {
		return UUID.randomUUID().toString();
	}
	
	public void setPrimaryID(T entity, String id) {
		if (this.primaryKeyField == null) {
            throw new IllegalStateException("This DataStore must either implement getPrimaryID() method, or add an @Id annotation to a field in the entity");
        }
        
        try {
            this.primaryKeyField.setAccessible(true);
            this.primaryKeyField.set(entity, id);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to access primary key field", e);
        }
	}
	
	protected final void massage(T entity, boolean isUpdateRequest) {
        long currentTime = System.currentTimeMillis();

        if (!isUpdateRequest && entity instanceof CreationTimeStampedEntity) {
            CreationTimeStampedEntity cte = (CreationTimeStampedEntity) entity;
            cte.setCreated(currentTime);
        }

        if (entity instanceof UpdateTimeStampedEntity) {
            UpdateTimeStampedEntity ute = (UpdateTimeStampedEntity) entity;
            ute.setUpdated(currentTime);
        }

        if (entity instanceof UserOwnedEntity) {
            UserOwnedEntity uoe = (UserOwnedEntity) entity;
            String entityUserID = uoe.getUserID();
            String currentUserID = SecurityContext.getUserID();

            if (entityUserID == null || entityUserID.equals(currentUserID)) {
                uoe.setUserID(currentUserID);
            } else {
                throw new SecurityException("Entity is owned by a different user");
            }
        }
    }
	
}
