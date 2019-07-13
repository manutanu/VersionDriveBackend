/*
* StorageUtilServiceImpl
* This service Contains Logic to store file in fileSystem
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.entity.FileStuff;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.UserRepository;

@Service
public class StorageUtilServiceImpl implements StorageUtilService,ConstantUtils {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private ViewShareDownloadService  viewShareDownloadService;
	
	@Value("${ROOT_DIR}")
	private String rootDir;

	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	
	/**
	 * @Description utility for storing file inthe database and inthe drive 
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param Multipart file, userid ,newname of the file
	 * @Exception 
	 * 
	 */
	@Transactional
	public void store(MultipartFile file, long userid, String newname) {
		
		try {
			
			//fetch userobj from database using userid and find the newname of the file to be uploaded in case of already existing file with same name 
			UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);
			
			//form the path of the new file 
			Path locationOfFile = Paths.get(rootDir + "/" + userid + "@" + userOb.getUsername());
			
			//if newname string is empty that means there is no file with this name in the drive 
			if (newname.equals("")) {
				
				Files.copy(file.getInputStream(), locationOfFile.resolve(file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
			
			} else {
				
				//store the file the new name 
				Files.copy(file.getInputStream(), locationOfFile.resolve(newname),StandardCopyOption.REPLACE_EXISTING);
			
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException("FAIL!");
		
		}
	
	}
	
	
	/**
	 * @Description utility for storing Versions of file into the drive 
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param Multipart filem, userid ,fileversionname
	 * @Exception 
	 * 
	 */
	@Transactional
	public void storeVersion(MultipartFile file, long userid, String fileversionname) {
		
		try {
			
			//fetch userobj using userid and make the path object using userid then copy the file into the location
			UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);
			Path locationOfFile = Paths.get(rootDir + "/" + userid + "@" + userOb.getUsername());
			Files.copy(file.getInputStream(), locationOfFile.resolve(fileversionname),StandardCopyOption.REPLACE_EXISTING);
		
		} catch (Exception e) {
			
			throw new RuntimeException("FAIL!");
		
		}
	
	}

	
	/**
	 * @Description utility for deleting file from the storage and database of the user
	 * 
	 * @Author Mritunjay Yadav
	 * @return boolean
	 * @param long userid, long fileid
	 * @Exception 
	 * 
	 */
	@Transactional
	public boolean deleteFile(long userid, long fileid) {
		
		//fetch fileobj and userobj from db using userid and fileid 
		boolean deleteflag = false;
		FileStuff fileobj = fileRepository.getOne(fileid);
		fileRepository.deleteById(fileid);
		UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);

		//forming the path to the file 
		Path locationOfFile = Paths
				.get(rootDir + "/" + userid + "@" + userOb.getUsername() + "/" + fileobj.getFilename());
		try {
			
			//delte the file from the storage and 
			FileSystemUtils.deleteRecursively(locationOfFile);
			viewShareDownloadService.insertTransaction("DELETED", fileobj.getFilename(), null, null, userid);
			deleteflag = true;
		
		} catch (IOException e) {
			
			e.printStackTrace();
			deleteflag = false;
		
		}
	
		return deleteflag;
	
	}
	
	
	/**
	 * @Description method to delete version of file 
	 * 
	 * @Author Mritunjay Yadav
	 * @return boolean 
	 * @param long userid, String filename
	 * @Exception 
	 * 
	 */
	@Transactional
	public boolean deleteFileVersion(long userid, String filename) {
		
		//fetch userobj from userid 
		boolean deleteflag = false;
		UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);

		//getting path to the file to be deleted
		Path locationOfFile = Paths
				.get(rootDir + "/" + userid + "@" + userOb.getUsername() + "/" + filename);
		
		try {
			
			//delete file and insert activity in transaction table
			FileSystemUtils.deleteRecursively(locationOfFile);
			viewShareDownloadService.insertTransaction("DELETED", filename, null, null, userid);
			deleteflag = true;
		
		} catch (IOException e) {
			
			//exception caught
			e.printStackTrace();
			deleteflag = false;
		
		}
		
		return deleteflag;
	
	}

}