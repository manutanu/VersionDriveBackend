package com.VersionDriveBackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.model.FileStuff;
import com.VersionDriveBackend.model.TransactionManagementStuff;
import com.VersionDriveBackend.model.VersionStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.TransactionRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.repository.VersionRepository;
import com.VersionDriveBackend.service.StorageUtilService;

@Controller
@CrossOrigin({"http://localhost:4100","http://localhost:4200"})
public class FileUploadController {

	//controller for storing the upcoming files in the request
	 @Autowired
	 private StorageUtilService storageService;
	 
	 @Autowired
	 private FileRepository fileRepository;
	 
	 @Autowired 
	 private UserRepository userRepository;
	 
	 @Autowired
	 private VersionRepository versionRepository;
	 
	 @Autowired
	 private TransactionRepository transactionRepository;
	 
	 public static int counter=0;
	 
	  List<String> files = new ArrayList<String>();
	 
	  @PostMapping("/upload/{userid}")
	  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,@PathVariable long userid) {
		
	    String message = "";
	    try {
	      message = "You successfully uploaded " + file.getOriginalFilename() + "!";
	      FileStuff fileobj= new FileStuff();
	      fileobj.setFilename(file.getOriginalFilename());
	      fileobj.setUser(userRepository.getOne(userid));
	      fileobj.setAtestVersion(1.0);
	      String newname="";
	      if(!CollectionUtils.isEmpty(fileRepository.getFileByFilename(file.getOriginalFilename()))) {
	    	  newname=counter+"@"+file.getOriginalFilename();
	    	  fileobj.setFilename(newname);
	    	  counter++;
	      }
	      fileRepository.save(fileobj);
	      storageService.store(file,userid,newname);
	      files.add(file.getOriginalFilename());
	      TransactionManagementStuff transaction=new TransactionManagementStuff();
			transaction.setActionTaken("UPLOAD");
			transaction.setFileName(file.getOriginalFilename());
//			transaction.set(uesrwhoshared.get());
			transaction.setUserid(userid);
			transactionRepository.save(transaction);
	      
	      return ResponseEntity.status(HttpStatus.OK).body(message);
	    } catch (Exception e) {
	      message = "FAIL to upload " + file.getOriginalFilename() + "!";
	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
	    }
	  }
	  
	 
	//contoller for  upload file versions
		@PostMapping("/uploadversion/{userid}/{fileid}")
		public ResponseEntity<String> uploadVersionOfFile(@RequestParam("file") MultipartFile file,@PathVariable long userid , @PathVariable long fileid){

		    String message = "";
		    try {

		      
		      //got original file object from database
		      FileStuff fileobj= fileRepository.getOne(fileid);
		      fileobj.setAtestVersion(fileobj.getAtestVersion()+1);
		      StringBuilder nameinreverse=new StringBuilder(fileobj.getFilename());
		      String nameinreverestring=nameinreverse.reverse().substring(nameinreverse.indexOf(".")+1, nameinreverse.length());
		      String ext=(new StringBuilder(nameinreverse.substring(0,nameinreverse.indexOf(".")))).reverse().toString();
		      StringBuilder sb=new StringBuilder(nameinreverestring);
		      String newfileversionname=sb.reverse().toString()+"v"+fileobj.getAtestVersion()+"."+ext;
		      System.out.println(newfileversionname);
		      VersionStuff versionFile=new VersionStuff();
		      versionFile.setVersionname(newfileversionname);
		      versionFile.setFileversion(fileobj);
		      
		      storageService.storeVersion(file,fileobj.getUser().getUserid(),newfileversionname);
		      fileRepository.save(fileobj);
		      versionRepository.save(versionFile);
		      
//		      files.add(file.getOriginalFilename());
		    
		      message = "You successfully uploaded " + file.getOriginalFilename() +" with name "+ newfileversionname + "!";
		      
		      TransactionManagementStuff transaction=new TransactionManagementStuff();
				transaction.setActionTaken("UPLOADVERSION");
				transaction.setFileName(newfileversionname);
//				transaction.set(uesrwhoshared.get());
				transaction.setUserid(userid);
				transactionRepository.save(transaction);
		      
		      return ResponseEntity.status(HttpStatus.OK).body(message);
		    } catch (Exception e) {
		      message = "FAIL to upload " + file.getOriginalFilename() + "!";
		      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		    }
		}
	
}
