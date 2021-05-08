package com.sangupta.reread.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.reread.core.AbstractEntityController;
import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.UserSettings;
import com.sangupta.reread.service.UserSettingsService;

@RestController
@RequestMapping("/settings")
public class UserSettingsController extends AbstractEntityController<UserSettings> {
	
	@Autowired
	protected UserSettingsService userSettingsService;

	@Override
	protected DataStoreService<UserSettings> getDataStoreService() {
		return this.userSettingsService;
	}

}
