/*
* ConstantUtils
* This Interface Contains constants which will be used throughout the application
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.constants;

import org.springframework.stereotype.Component;

@Component
public interface ConstantUtils {

	
	
	public final int ACTIVATED=1;
	
	public final int NOT_ACTIVATED=0;
	
	public final String ROOT_DIR="uploads";
	
	public static final int EXPIRATION = 60;
	
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
}
