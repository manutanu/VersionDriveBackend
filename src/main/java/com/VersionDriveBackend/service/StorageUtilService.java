package com.VersionDriveBackend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.model.FileStuff;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.UserRepository;

@Service
public class StorageUtilService implements ConstantUtils {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileRepository fileRepository;

	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Transactional
	public void store(MultipartFile file,long userid) {
    try {
    UserStuff userObject = userRepository.getOne(userid);
      Path locationOfFile = Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername());
      Files.copy(file.getInputStream(), locationOfFile.resolve(file.getOriginalFilename()));
    } catch (Exception e) {
      throw new RuntimeException("FAIL!");
    }
  }
	

	@Transactional
	public void storeVersion(MultipartFile file,long userid,String fileversionname) {
    try {
    UserStuff userObject = userRepository.getOne(userid);
      Path locationOfFile = Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername());
      Files.copy(file.getInputStream(), locationOfFile.resolve(fileversionname));
    } catch (Exception e) {
      throw new RuntimeException("FAIL!");
    }
  }

//	public Resource loadFile(String filename,long userid) {
//    try {
//    	UserStuff userObject = userRepository.getOne(userid);
//    	Path locationOfFile = Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername());
//      Path file = locationOfFile.resolve(filename);
//      Resource resource = new UrlResource(file.toUri());
//      if (resource.exists() || resource.isReadable()) {
//        return resource;
//      } else {
//        throw new RuntimeException("FAIL!");
//      }
//    } catch (MalformedURLException e) {
//      throw new RuntimeException("FAIL!");
//    }
//  }
	@Transactional
	public boolean deleteFile(long userid,long fileid) {
		boolean deleteflag=false;
		FileStuff fileobj=fileRepository.getOne(fileid);
		fileRepository.deleteById(fileid);
		UserStuff userObject = userRepository.getOne(userid);
		
    	Path locationOfFile = Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername()+"/"+fileobj.getFilename());
		try {
			FileSystemUtils.deleteRecursively(locationOfFile);
			deleteflag=true;
		} catch (IOException e) {
			e.printStackTrace();
			deleteflag=false;
		}
		return deleteflag;
	}

}