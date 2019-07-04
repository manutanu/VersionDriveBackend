package com.VersionDriveBackend.service;

import java.util.List;
import java.util.Map;

import com.VersionDriveBackend.dto.ResponseFileObject;
import com.VersionDriveBackend.dto.ResponseSharedFileVO;
import com.VersionDriveBackend.dto.ShareRequest;
import com.VersionDriveBackend.model.FileStuff;
import com.VersionDriveBackend.model.TransactionManagementStuff;
import com.VersionDriveBackend.model.UserStuff;

public interface ViewShareDownloadService {
	
	public List<ResponseFileObject> getAllFilesInSortedOrderOfInsertion(long userid);
	
	public UserStuff getUserByUseridAndVerified(long userid, int activated) ;
	
	public FileStuff getFileByFileid(long fileid);
	
	public UserStuff getUserByEmailAndVerified(String email, int activated);
	
	public Map<String, String> shareFileUtility(ShareRequest request);
	
	public List<ResponseSharedFileVO> utilityForShowingSharedFiles(long userid) ;
	
	public List<UserStuff> getAllUserByVerified(int activated);
	
	public List<TransactionManagementStuff> getAllByuserid(long userid);

}
