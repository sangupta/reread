package com.sangupta.reread.entity;

import com.sangupta.jerry.entity.UserAwarePrincipal;

public class User implements UserAwarePrincipal {
	
	public String userID;
	
	public String name;
	
	public User() {
		
	}
	
	public User(String userID, String name) {
		this.userID = userID;
		this.name = name;
	}

	@Override
	public String getUserID() {
		return this.userID;
	}

	@Override
	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
