/*
* FileUploadService
* This Interface is for fileUpload related functioning
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.model.VersionStuff;

public interface FileUploadService {

	public Map<String,String> uploadingNewFile(MultipartFile file,long userid) ;
	
	public VersionStuff uploadingVersionOfFile(MultipartFile file,long userid,long fileid);
}
