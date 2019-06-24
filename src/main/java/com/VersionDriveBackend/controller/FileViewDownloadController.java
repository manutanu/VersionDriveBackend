package com.VersionDriveBackend.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.model.FileStuff;
import com.VersionDriveBackend.model.ResponseFileObject;
import com.VersionDriveBackend.model.Share;
import com.VersionDriveBackend.model.ShareRequest;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.ShareRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.service.StorageUtilService;

@Controller
@RequestMapping("/viewdownload")
@CrossOrigin("http://localhost:4200")
public class FileViewDownloadController implements ConstantUtils{

	@Autowired
	private StorageUtilService storageService;
	
	@Autowired
	private UserRepository userRepository;
	

	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private ShareRepository shareRepository;
	

	

	// controller for listing all files
	@GetMapping("/getallfiles/{userid}")
	public ResponseEntity<List<ResponseFileObject>> getListFiles(Model model, @PathVariable long userid) {
		List<ResponseFileObject> fileNames = new ArrayList<>();
		UserStuff userobject = userRepository.getOne(userid);
		userobject.getFileList().forEach(filestuff -> {
			ResponseFileObject responob=new ResponseFileObject(filestuff.getFileid(), filestuff.getFilename(), filestuff.getCreationDate(), filestuff.getUpdationDate());
			fileNames.add(responob);
		});
		return ResponseEntity.ok().body(fileNames);
	}
	
	@RequestMapping("/view/{userid}/{fileid}")
	public void viewResource(HttpServletRequest request, HttpServletResponse response,
			@PathVariable long fileid, @PathVariable long userid) throws IOException {
		UserStuff userObject = userRepository.getOne(userid);
		FileStuff fileByFileid = fileRepository.getFileByFileid(fileid);
		File file = new File(ROOT_DIR+"/"+userid+"@"+userObject.getUsername()+"/"+fileByFileid.getFilename());
		if (file.exists()) {
			//get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				//unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			 //Here we have mentioned it to show as attachment
			 //response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}
	

	// controller for getting files for download purpose
	@GetMapping("/download/{userid}/{fileid}")
	@ResponseBody
	public void downloadResource(HttpServletRequest request, HttpServletResponse response,
			@PathVariable long fileid, @PathVariable long userid) throws IOException {
		UserStuff userObject = userRepository.getOne(userid);
		FileStuff fileByFileid = fileRepository.getFileByFileid(fileid);
		File file = new File(ROOT_DIR+"/"+userid+"@"+userObject.getUsername()+"/"+fileByFileid.getFilename());
		if (file.exists()) {
			//get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				//unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

			 //Here we have mentioned it to show as attachment
			 //response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}

	// controller for deleting file

	// controller for showing shared files

	// controller for sharing files
	@PostMapping("/share/{fileid}")
	public ResponseEntity<String> shareThisFile(@RequestBody ShareRequest request){
		try {
			System.out.println(request.toString());
			UserStuff usertobeshared=userRepository.getUserByEmail(request.getToemail());
			Optional<UserStuff> uesrwhoshared=userRepository.findById(request.getFromuserid());
			FileStuff filewhichisshared=fileRepository.getOne(request.getFileid());
			Share sharetransaction=new Share();
			sharetransaction.setFromid(uesrwhoshared.get().getUserid());
			sharetransaction.setToid(usertobeshared.getUserid());
			sharetransaction.setFileshare(filewhichisshared);
			sharetransaction.setPermission(request.getPermission());
			shareRepository.save(sharetransaction);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().body("SUCCESS");
	}
	
	@GetMapping("/shared/{userid}")
	public ResponseEntity<List<ResponseFileObject>> getSharedFilesOfUser(@PathVariable("userid") long userid ){
		List<ResponseFileObject> listofsharedfilestothisuser=new ArrayList<>();
		
		List<Share> shareobject=shareRepository.getShareByToid(userid);
		
		shareobject.forEach(shareo -> {
			ResponseFileObject responob=new ResponseFileObject(shareo.getFileshare().getFileid(), shareo.getFileshare().getFilename(), shareo.getFileshare().getCreationDate(), shareo.getFileshare().getUpdationDate());
			listofsharedfilestothisuser.add(responob);
		});
		
		return ResponseEntity.ok().body(listofsharedfilestothisuser);
	}
	
	@GetMapping("/getallUserdetails")
	public ResponseEntity<List<String>> getAllUserDetails(){
		List<UserStuff> listofuser=userRepository.findAll();
		List<String> emaillist=new ArrayList<>();
		for(int i=0;i<listofuser.size();i++) {
			emaillist.add(listofuser.get(i).getEmail());
		}
		return ResponseEntity.ok().body(emaillist);
	}
	

	//

}

// controller for getting files for preview purpose
//@GetMapping("/view/{userid}/{fileid}")
//@ResponseBody
//public ResponseEntity<Resource> getFileForPreview(@PathVariable long fileid, @PathVariable long userid) {
//	FileStuff fileByFileid = fileRepository.getFileByFileid(fileid);
//	Resource file = storageService.loadFile(fileByFileid.getFilename(), userid);
//
//	return ResponseEntity.ok()
//			.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"").body(file);
//	
//	
//	
//}
