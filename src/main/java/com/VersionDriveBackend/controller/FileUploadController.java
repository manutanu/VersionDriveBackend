package com.VersionDriveBackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.model.FileStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.service.StorageUtilService;

@Controller
@CrossOrigin("http://localhost:4200")
public class FileUploadController {

	//controller for storing the upcoming files in the request
	 @Autowired
	 private StorageUtilService storageService;
	 
	 @Autowired
	 private FileRepository fileRepository;
	 
	 @Autowired 
	 private UserRepository userRepository;
	 
	 
	 
	  List<String> files = new ArrayList<String>();
	 
	  @PostMapping("/upload/{userid}")
	  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,@PathVariable long userid) {
		
	    String message = "";
	    try {
	      storageService.store(file,userid);
	      files.add(file.getOriginalFilename());
	 
	      message = "You successfully uploaded " + file.getOriginalFilename() + "!";
	      FileStuff fileobj= new FileStuff();
	      fileobj.setFilename(file.getOriginalFilename());
	      fileobj.setUser(userRepository.getOne(userid));
	      fileRepository.save(fileobj);
	      return ResponseEntity.status(HttpStatus.OK).body(message);
	    } catch (Exception e) {
	      message = "FAIL to upload " + file.getOriginalFilename() + "!";
	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
	    }
	  }
	 
	  
	
}
