/*
* FileUploadController
* This Class is controller for all apis related to uploading file
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.entity.VersionStuff;
import com.VersionDriveBackend.service.FileUploadService;

@Controller
@CrossOrigin({ "http://localhost:4100", "http://localhost:4200","http://192.168.43.195:4200" })
public class FileUploadController implements ConstantUtils{

	/* controller for storing the upcoming files in the request */

	@Autowired
	private FileUploadService fileUploadService;

	List<String> files = new ArrayList<String>();

	@PostMapping("/upload/{userid}")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@PathVariable long userid) {

		String message = "";
		Map<String,String> response=new HashMap<>();
		message=response.get("message");
		response=fileUploadService.uploadingNewFile(file, userid);
		if(response.get("status").equals("ERROR")) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}else {
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
			
	}

	/* contoller for upload file versions */
	@PostMapping("/uploadversion/{userid}/{fileid}")
	@ResponseBody
	public VersionStuff uploadVersionOfFile(@RequestParam("file") MultipartFile file, @PathVariable long userid,
			@PathVariable long fileid) {
		Map<String, String> response = new HashMap<>();
//		String message = "";
		VersionStuff version=fileUploadService.uploadingVersionOfFile(file,userid,fileid);
		return version;
	}

}
