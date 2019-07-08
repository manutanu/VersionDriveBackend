/*
* JwtResponse
*  This class acts as VO to hold data which is used in sending response for authentication api
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.dto;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final long userid;
	private final String username;
	

	public JwtResponse(String jwttoken,long userid,String username) {
		this.jwttoken = jwttoken;
		this.userid=userid;
		this.username=username;
	}

	public String getToken() {
		return this.jwttoken;
	}

	public long getUserid() {
		return userid;
	}
	
}
