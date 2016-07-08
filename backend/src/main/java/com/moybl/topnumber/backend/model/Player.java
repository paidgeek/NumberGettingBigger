package com.moybl.topnumber.backend.model;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

@Entity
@Cache(expirationSeconds = 60 * 5)
public class Player {

	@Id
	private String id;
	private String sessionToken;
	@Index
	private double number;
	private String name;
	@Ignore
	private long logInTime;

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

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLogInTime(long logInTime) {
		this.logInTime = logInTime;
	}

	public long getLogInTime() {
		return logInTime;
	}

}
