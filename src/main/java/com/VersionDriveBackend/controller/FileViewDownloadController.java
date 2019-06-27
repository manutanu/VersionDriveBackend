package com.VersionDriveBackend.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.VersionDriveBackend.model.ResponseSharedFileVO;
import com.VersionDriveBackend.model.Share;
import com.VersionDriveBackend.model.ShareRequest;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.model.VersionStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.ShareRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.service.StorageUtilService;

@Controller
@RequestMapping("/viewdownload")
@CrossOrigin({"http://localhost:4100","http://localhost:4200"})
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
		for(int i=0;i<userobject.getFileList().size();i++) {
			userobject.getFileList().get(i).setUser(null);
			for(Share ss:userobject.getFileList().get(i).getSharelist()) {
				ss.setFileshare(null);
			}
			for(VersionStuff vv:userobject.getFileList().get(i).getVersionlist()) {
				vv.setFileversion(null);
			}
		}
		userobject.getFileList().forEach(filestuff -> {
			ResponseFileObject responob=new ResponseFileObject(filestuff.getFileid(), filestuff.getFilename(), filestuff.getCreationDate(), filestuff.getUpdationDate(),filestuff.getSharelist(),filestuff.getVersionlist());
			fileNames.add(responob);
		});
		return ResponseEntity.ok().body(fileNames);
	}
	
	
	// controller for getting files for preview purpose
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
	
	// controller for sharing files
	@PostMapping("/share/{fileid}")
	public ResponseEntity<?> shareThisFile(@RequestBody ShareRequest request){
		Map<String,String> responsemap=new HashMap<>();
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
			responsemap.put("status","SUCCESS");
		}catch(Exception e) {
			e.printStackTrace();
			responsemap.put("status","ERROR");
		}
		//HttpHeaders headers=new HttpHeaders();
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}
	
	// controller for showing shared files
	@GetMapping("/shared/{userid}")
	public ResponseEntity<List<ResponseSharedFileVO>> getSharedFilesOfUser(@PathVariable("userid") long userid ){
		List<ResponseSharedFileVO> listofsharedfilestothisuser=new ArrayList<>();
		
		List<Share> shareobject=shareRepository.getShareByToid(userid);
		
		shareobject.forEach(shareo -> {
			UserStuff fromuserobject=userRepository.getOne(shareo.getFromid());
			ResponseSharedFileVO responob=new ResponseSharedFileVO(shareo.getFileshare().getFileid(), shareo.getFileshare().getFilename(), shareo.getFileshare().getCreationDate(), shareo.getFileshare().getUpdationDate(),shareo.getFromid(),fromuserobject.getUsername(),fromuserobject.getEmail(),shareo.getPermission().toUpperCase());
			listofsharedfilestothisuser.add(responob);
		});
		
		return ResponseEntity.ok().body(listofsharedfilestothisuser);
	}
	
	//getting all user details for creating typeaheads
	@GetMapping("/getallUserdetails")
	public ResponseEntity<List<String>> getAllUserDetails(){
		List<UserStuff> listofuser=userRepository.findAll();
		List<String> emaillist=new ArrayList<>();
		for(int i=0;i<listofuser.size();i++) {
			emaillist.add(listofuser.get(i).getEmail());
		}
		return ResponseEntity.ok().body(emaillist);
	}
	
	// controller for deleting file
	@GetMapping("/deletefile/{userid}/{fileid}")
	public Map<String, String> deleteThisFile(@PathVariable("userid") long userid , @PathVariable("fileid") long fileid){
		boolean flag=storageService.deleteFile(userid, fileid);
		Map<String,String> response = new HashMap<String,String>();
		if(flag==true) {
			response.put("status","SUCCESS");
		}else {
			response.put("status","ERROR");
		}
		return response;
	}
	
	
	

}


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
