/*
* StorageUtilService
* This Interface Contains method signature for StorageUtil Service
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageUtilService {
	
	public void store(MultipartFile file, long userid, String newname) ;
	
	public void storeVersion(MultipartFile file, long userid, String fileversionname) ;
	
	public boolean deleteFile(long userid, long fileid) ;
	
	public boolean deleteFileVersion(long userid, String filename) ;

}
