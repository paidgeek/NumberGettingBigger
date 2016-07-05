package com.moybl.topnumber.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Player {

	@Id
	private String id;
	private String sessionToken;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getSessionToken() {
		return sessionToken;
	}

}
