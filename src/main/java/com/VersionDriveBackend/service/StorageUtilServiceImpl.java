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
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.entity.FileStuff;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.TransactionRepository;
import com.VersionDriveBackend.repository.UserRepository;

@Service
public class StorageUtilServiceImpl implements StorageUtilService,ConstantUtils {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private ViewShareDownloadService  viewShareDownloadService;

	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Description 
	 * @param
	 * @return
	 * 	 * 
	 * */
	@Transactional
	public void store(MultipartFile file, long userid, String newname) {
		
		try {
			
			UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);
			Path locationOfFile = Paths.get(ROOT_DIR + "/" + userid + "@" + userOb.getUsername());
			
			if (newname.equals("")) {
				
				Files.copy(file.getInputStream(), locationOfFile.resolve(file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
			} else {
				
				Files.copy(file.getInputStream(), locationOfFile.resolve(newname),StandardCopyOption.REPLACE_EXISTING);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException("FAIL!");
		}
	}

	@Transactional
	public void storeVersion(MultipartFile file, long userid, String fileversionname) {
		try {
			UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);
			Path locationOfFile = Paths.get(ROOT_DIR + "/" + userid + "@" + userOb.getUsername());
			Files.copy(file.getInputStream(), locationOfFile.resolve(fileversionname),StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw new RuntimeException("FAIL!");
		}
	}

	/*
	 * public Resource loadFile(String filename,long userid) { try { UserStuff
	 * userObject = userRepository.getOne(userid); Path locationOfFile =
	 * Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername()); Path file =
	 * locationOfFile.resolve(filename); Resource resource = new
	 * UrlResource(file.toUri()); if (resource.exists() || resource.isReadable()) {
	 * return resource; } else { throw new RuntimeException("FAIL!"); } } catch
	 * (MalformedURLException e) { throw new RuntimeException("FAIL!"); } }
	 */

	@Transactional
	public boolean deleteFile(long userid, long fileid) {
		boolean deleteflag = false;
		FileStuff fileobj = fileRepository.getOne(fileid);
		fileRepository.deleteById(fileid);
		UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);

		Path locationOfFile = Paths
				.get(ROOT_DIR + "/" + userid + "@" + userOb.getUsername() + "/" + fileobj.getFilename());
		try {
			FileSystemUtils.deleteRecursively(locationOfFile);
			viewShareDownloadService.insertTransaction("DELETED", fileobj.getFilename(), null, null, userid);
			deleteflag = true;
		} catch (IOException e) {
			e.printStackTrace();
			deleteflag = false;
		}
		return deleteflag;
	}
	
	@Transactional
	public boolean deleteFileVersion(long userid, String filename) {
		boolean deleteflag = false;
//		FileStuff fileobj = fileRepository.getOne(fileid);
//		fileRepository.deleteById(fileid);
		UserStuff userOb = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);

		Path locationOfFile = Paths
				.get(ROOT_DIR + "/" + userid + "@" + userOb.getUsername() + "/" + filename);
		try {
			FileSystemUtils.deleteRecursively(locationOfFile);
			viewShareDownloadService.insertTransaction("DELETED", filename, null, null, userid);
			deleteflag = true;
		} catch (IOException e) {
			e.printStackTrace();
			deleteflag = false;
		}
		return deleteflag;
	}

}