package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.UserSettings;
import com.sangupta.reread.redis.RedisDataStoreServiceImpl;
import com.sangupta.reread.service.UserSettingsService;

@Service
public class RedisUserSettingsServiceImpl extends RedisDataStoreServiceImpl<UserSettings> implements UserSettingsService {

}
