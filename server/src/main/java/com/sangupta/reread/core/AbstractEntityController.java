package com.sangupta.reread.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;

public abstract class AbstractEntityController<T, X> {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityController.class);

    protected abstract DataStoreService<T, X> getDataStoreService();

    protected final Class<T> entityClass;

    protected final Class<X> primaryIDClass;

    protected final boolean isUserOwnedEntity;

    @SuppressWarnings("unchecked")
    protected AbstractEntityController() {
        // extract entity class and primary key class
        Type t = getClass().getGenericSuperclass();
        if (!(t instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Instance is not parameterized.");
        }

        Type[] actualTypeArguments = ((ParameterizedType) t).getActualTypeArguments();
        this.entityClass = (Class<T>) actualTypeArguments[0];
        this.primaryIDClass = (Class<X>) actualTypeArguments[1];

        this.isUserOwnedEntity = UserOwnedEntity.class.isAssignableFrom(this.entityClass);
    }

    protected boolean isEntityUserOwned() {
        return false;
    }

    @GetMapping("")
    public List<T> getAll(HttpServletRequest request) {
        String userID = SecurityContext.getUserID();
        if (AssertUtils.isEmpty(userID)) {
            throw new HttpException(HttpStatusCode.UNAUTHORIZED, "Sign-in required");
        }

        if (this.isUserOwnedEntity) {
            return this.getDataStoreService().getAllForUser(SecurityContext.getUserID());
        }

        // return everything in database
        return this.getDataStoreService().getAll();
    }

    @GetMapping("/{entityID}")
    public T getEntity(@PathVariable X entityID) {
        if (AssertUtils.isEmpty(entityID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity ID is required");
        }

        T entity = this.getDataStoreService().get(entityID);
        if (entity == null) {
            throw new HttpException(HttpStatusCode.NOT_FOUND, "No entity exists with that ID");
        }

        if (!this.isEntityOwnedByUser(entity)) {
            throw new HttpException(HttpStatusCode.FORBIDDEN, "Not the owner of the entity");
        }

        return entity;
    }

    @PutMapping("")
    public T insertEntity(@RequestBody T entity) {
        if (entity == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity is required");
        }

        X primaryID = this.getDataStoreService().getPrimaryID(entity);
        if (AssertUtils.isNotEmpty(primaryID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity cannot have an ID, use update instead");
        }

        boolean inserted = this.getDataStoreService().insert(entity);
        if (inserted) {
            return entity;
        }

        LOGGER.warn("Unable to insert entity");
        throw new HttpException(HttpStatusCode.SERVICE_UNAVAILABLE, "Unable to insert the entity");
    }

    @PostMapping("/{entityID}")
    public T updateEntity(@PathVariable X entityID, @RequestBody T entity) {
        if (entity == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity is required");
        }
        
        if(AssertUtils.isEmpty(entityID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "EntityID is required");
        }
        
        final X primaryID = this.getDataStoreService().getPrimaryID(entity);
        if (AssertUtils.isEmpty(primaryID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity needs an ID to be updated");
        }
        
        if(!entityID.equals(primaryID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "EntityID mismatch in path and payload");
        }

        T existing = this.getDataStoreService().get(primaryID);
        if (existing == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "No entity exists with that ID");
        }

        if (!this.isEntityOwnedByUser(existing)) {
            throw new HttpException(HttpStatusCode.FORBIDDEN, "Not the owner of the entity");
        }

        boolean updated = this.getDataStoreService().update(entity);
        if (updated) {
            return entity;
        }

        LOGGER.warn("Unable to update entity");
        throw new HttpException(HttpStatusCode.SERVICE_UNAVAILABLE, "Unable to update the entity");
    }

    @DeleteMapping("/{entityID}")
    public T deleteEntity(@PathVariable X entityID) {
        if (AssertUtils.isEmpty(entityID)) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Entity ID is required");
        }

        T existing = this.getDataStoreService().get(entityID);
        if (existing == null) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "No entity exists with that ID");
        }

        if (!this.isEntityOwnedByUser(existing)) {
            throw new HttpException(HttpStatusCode.FORBIDDEN, "Not the owner of the entity");
        }

        return this.getDataStoreService().delete(entityID);
    }

    protected boolean isEntityOwnedByUser(T entity) {
        if (!this.isUserOwnedEntity) {
            return true;
        }

        String userID = SecurityContext.getUserID();
        if (userID == null) {
            return false;
        }

        UserOwnedEntity uoe = (UserOwnedEntity) entity;
        return userID.equals(uoe.getUserID());
    }

}
