package com.VersionDriveBackend.model;

public class UserResponseObject {

	private String username;
	private String email;
	private long userid;
	
	public UserResponseObject() {}
	
	public UserResponseObject(String username, String email, long userid) {
		super();
		this.username = username;
		this.email = email;
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	
	
}
