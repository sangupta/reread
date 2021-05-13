package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.redislabs.modules.rejson.JReJSON;
import com.sangupta.reread.entity.UserSettings;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.UserSettingsService;

/**
 * Redis {@link JReJSON} based implementation of the {@link UserSettingsService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisUserSettingsServiceImpl extends RedisDataStoreServiceImpl<UserSettings> implements UserSettingsService {

}
