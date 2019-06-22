package com.VersionDriveBackend.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.VersionDriveBackend.model.FileStuff;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.service.StorageUtilService;

@Controller
@RequestMapping("/viewdownload")
@CrossOrigin("http://localhost:4200")
public class FileViewDownloadController {

	@Autowired
	private StorageUtilService storageService;
	
	@Autowired
	private UserRepository userRepository;
	

	@Autowired
	private FileRepository fileRepository;

	

	// controller for listing all files
	@GetMapping("/getallfiles/{userid}")
	public ResponseEntity<List<String>> getListFiles(Model model, @PathVariable long userid) {
		List<String> fileNames = new ArrayList<String>();
		UserStuff userobject = userRepository.getOne(userid);
		userobject.getFileList().forEach(filestuff -> {
			fileNames.add(filestuff.getFilename());
		});
		return ResponseEntity.ok().body(fileNames);
	}

	// controller for getting files for preview purpose
	@GetMapping("/view/{userid}/{fileid}")
	@ResponseBody
	public ResponseEntity<Resource> getFileForPreview(@PathVariable long fileid, @PathVariable long userid) {
		FileStuff fileByFileid = fileRepository.getFileByFileid(fileid);
		Resource file = storageService.loadFile(fileByFileid.getFilename(), userid);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"").body(file);
	}

	// controller for getting files for download purpose
	@GetMapping("/download/{userid}/{fileid}")
	@ResponseBody
	public ResponseEntity<Resource> getFileForDownload(@PathVariable long fileid, @PathVariable long userid) {
		FileStuff fileByFileid = fileRepository.getFileByFileid(fileid);
		Resource file = storageService.loadFile(fileByFileid.getFilename(), userid);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	// controller for deleting file

	// controller for showing shared files

	// controller for sharing files

	//

}
