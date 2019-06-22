package com.VersionDriveBackend.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.repository.UserRepository;

@Service
public class StorageUtilService implements ConstantUtils {

	@Autowired
	private UserRepository userRepository;

	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public void store(MultipartFile file,long userid) {
    try {
    UserStuff userObject = userRepository.getOne(userid);
      Path locationOfFile = Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername());
      Files.copy(file.getInputStream(), locationOfFile.resolve(file.getOriginalFilename()));
    } catch (Exception e) {
      throw new RuntimeException("FAIL!");
    }
  }

	public Resource loadFile(String filename,long userid) {
    try {
    	UserStuff userObject = userRepository.getOne(userid);
    	Path locationOfFile = Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername());
      Path file = locationOfFile.resolve(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("FAIL!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("FAIL!");
    }
  }

	public void deleteAll(long userid) {
		UserStuff userObject = userRepository.getOne(userid);
    	Path locationOfFile = Paths.get(ROOT_DIR+"/"+userid+"@"+userObject.getUsername());
		FileSystemUtils.deleteRecursively(locationOfFile.toFile());
	}

}