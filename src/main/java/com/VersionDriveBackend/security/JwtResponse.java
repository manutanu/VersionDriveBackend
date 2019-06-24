package com.VersionDriveBackend.security;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final long userid;
	

	public JwtResponse(String jwttoken,long userid) {
		this.jwttoken = jwttoken;
		this.userid=userid;
	}

	public String getToken() {
		return this.jwttoken;
	}

	public long getUserid() {
		return userid;
	}
	
}
