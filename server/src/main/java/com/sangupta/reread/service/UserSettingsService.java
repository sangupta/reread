package com.sangupta.reread.service;

import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.UserSettings;

/**
 * Service that allows to persist {@link UserSettings} in the data store.
 * 
 * @author sangupta
 *
 */
public interface UserSettingsService extends DataStoreService<UserSettings> { 

}
