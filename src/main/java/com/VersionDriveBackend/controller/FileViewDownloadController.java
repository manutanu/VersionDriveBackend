/*
* FileViewDownloadController
* This Class is controller for all apis related to share , view , delete and download of files
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.VersionDriveBackend.dto.ResponseFileObject;
import com.VersionDriveBackend.dto.ResponseSharedFileVO;
import com.VersionDriveBackend.dto.ShareRequest;
import com.VersionDriveBackend.dto.UserResponseObject;
import com.VersionDriveBackend.entity.FileStuff;
import com.VersionDriveBackend.entity.TransactionManagementStuff;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.service.StorageUtilService;
import com.VersionDriveBackend.service.ViewShareDownloadService;


@Controller
@RequestMapping("/viewdownload")
@CrossOrigin({ "http://localhost:4100", "http://localhost:4200","http://192.168.1.106:4200" })
public class FileViewDownloadController implements ConstantUtils {

	@Autowired
	private ViewShareDownloadService viewShareDownloadService;
	
	@Autowired
	private StorageUtilService storageService;
	
	@Value("${ROOT_DIR}")
	private String rootDir;

	
	/**
	 * @Description  controller for listing all files
	 * 
	 * @Author Mritunjay Yadav
	 * @return List of ResponseFileObject 
	 * @param Model object and userid
	 * @Exception none
	 * 
	 * */
	@GetMapping("/getallfiles/{userid}")
	public ResponseEntity<List<ResponseFileObject>> getListFiles(Model model, @PathVariable long userid) {
		
		List<ResponseFileObject> fileNames = new ArrayList<>();
		fileNames=viewShareDownloadService.getAllFilesInSortedOrderOfInsertion(userid);
		return ResponseEntity.ok().body(fileNames);
	
	}

	

	/**
	 * @Description  controller for getting files for preview purpose from fileid
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param HttpServletRequest and HttpServletResponse
	 * @Exception IOException
	 * 
	 * */
	@RequestMapping("/view/{userid}/{fileid}")
	public void viewResource(HttpServletRequest request, HttpServletResponse response, @PathVariable long fileid,
			@PathVariable long userid) throws IOException {
		
		//fetching user object with userid and activated
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		//fetching fileObject with fileid
		FileStuff fileByFileid = viewShareDownloadService.getFileByFileid(fileid);
		//get File object 
		File file = new File(rootDir + "/" + userid + "@" + userob.getUsername() + "/" + fileByFileid.getFilename());
		
		//check if file exists or not 
		if (file.exists()) {
			
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			
			if (mimeType == null) {
				
				//unknown mimetype so set the mimetype to application/octet-stream 
				mimeType = "application/octet-stream";
			
			}
			
			//setting response according to the logic
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
			response.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			//setting inputStream from the file  into response 
			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
		
	}


	/**
	 * @Description  controller for getting Version of files for preview purpose from filename
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param HttpServletRequest and HttpServletResponse
	 * @Exception IOException
	 * 
	 * */
	@RequestMapping("/viewversion/{userid}/{filename}")
	public void viewVersionFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String filename,
			@PathVariable long userid) throws IOException {
		
		//fetching user object using userid and activated flag
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		//fetch file object 
		File file = new File(rootDir + "/" + userid + "@" + userob.getUsername() + "/" + filename);
		
		if (file.exists()) {
			
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			
			if (mimeType == null) {
			
				//unknown mimetype so set the mimetype to application/octet-stream 
				mimeType = "application/octet-stream";
			
			}
			
			//setting response 
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
			response.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
		
	}

	
	/**
	 * @Description  controller for getting files for download purpose
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param HttpServletRequest , HttpServletResponse , fileid  and userid 
	 * @Exception IOException
	 * 
	 * */
	@GetMapping("/download/{userid}/{fileid}")
	@ResponseBody
	public void downloadResource(HttpServletRequest request, HttpServletResponse response, @PathVariable long fileid,
			@PathVariable long userid) throws IOException {
		
		//fetching userobject and fileobject using userid and fileid and form the File object 
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		FileStuff fileByFileid = viewShareDownloadService.getFileByFileid(fileid);
		File file = new File(rootDir + "/" + userid + "@" + userob.getUsername() + "/" + fileByFileid.getFilename());
		
		if (file.exists()) {
			
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			
			if (mimeType == null) {
			
				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			
			}
			
			//setting response and inputstream from file
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));
			response.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());
			
			//inserting activity into transaction table
			userob.setFileList(null);
			viewShareDownloadService.insertTransaction("DOWNLOAD",fileByFileid.getFilename(),null,null,userob.getUserid());
			
		}

	}


	/**
	 * @Description  controller for getting file versions for download purpose using filename
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param HttpServletRequest , HttpServletResponse , fileid , filename and userid 
	 * @Exception IOException
	 * 
	 * */
	@GetMapping("/downloadversion/{userid}/{filename}")
	@ResponseBody
	public void downloadFileVersions(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String filename, @PathVariable long userid) throws IOException {
		
		//fetching user object and forming File object 
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		File file = new File(rootDir + "/" + userid + "@" + userob.getUsername() + "/" + filename);
		
		if (file.exists()) {
		
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			
			if (mimeType == null) {
			
				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			
			}
			
			//setting inputFileStream into response object 
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));
			response.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());
			
			//insert activity into transaction table
			userob.setFileList(null);
			viewShareDownloadService.insertTransaction("DOWNLOAD",filename,null,null,userob.getUserid());
		
		}
		
	}

	
	/**
	 * @Description  controller for sharing files
	 * 
	 * @Author Mritunjay Yadav
	 * @return Map<String,String>
	 * @param ShareRequest object
	 * @Exception 
	 * 
	 * */
	@PostMapping("/share/{fileid}")
	@ResponseBody
	public Map<String, String> shareThisFile(@RequestBody ShareRequest request) {
		
		return viewShareDownloadService.shareFileUtility(request);
	
	}


	/**
	 * @Description  controller for showing shared files
	 * 
	 * @Author Mritunjay Yadav
	 * @return ResponseEntity of List of ResponseSharedFileVO objects
	 * @param userid
	 * @Exception 
	 * 
	 * */
	@GetMapping("/shared/{userid}")
	public ResponseEntity<List<ResponseSharedFileVO>> getSharedFilesOfUser(@PathVariable("userid") long userid) {
		
		List<ResponseSharedFileVO> listofsharedfilestothisuser = new ArrayList<>();
		listofsharedfilestothisuser=viewShareDownloadService.utilityForShowingSharedFiles(userid);
		return ResponseEntity.ok().body(listofsharedfilestothisuser);
	
	}


	/**
	 * @Description  getting all user details for creating typeaheads
	 * 
	 * @Author Mritunjay Yadav
	 * @return ResponseEntity of List of String
	 * @param 
	 * @Exception 
	 * 
	 * */
	@GetMapping("/getallUserdetails")
	public ResponseEntity<List<String>> getAllUserDetails() {
		
		//fetching all users which are activated
		List<UserStuff> listofuser = viewShareDownloadService.getAllUserByVerified(ACTIVATED);
		List<String> emaillist = new ArrayList<>();
		
		for (int i = 0; i < listofuser.size(); i++) {
		
			//creating list of emails to show in typeahead
			emaillist.add(listofuser.get(i).getEmail());
		
		}
		
		return ResponseEntity.ok().body(emaillist);
	
	}


	/**
	 * @Description  controller for getting userdetail for particular user
	 * 
	 * @Author Mritunjay Yadav
	 * @return UserResponseObject
	 * @param userid
	 * @Exception 
	 * 
	 * */
	@GetMapping("/getUserProfile/{userid}")
	@ResponseBody
	public UserResponseObject getUserProfileDetails(@PathVariable long userid) {
		
		//fetching and sending user details using userid
		UserStuff temp = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		UserResponseObject userStuff = new UserResponseObject();
		userStuff.setEmail(temp.getEmail());
		userStuff.setUsername(temp.getUsername());
		return userStuff;
	
	}

	
	/**
	 * @Description  controller for deleting file
	 * 
	 * @Author Mritunjay Yadav
	 * @return Map<String,String>
	 * @param userid and fileid
	 * @Exception 
	 * 
	 * */
	@GetMapping("/deletefile/{userid}/{fileid}")
	public Map<String, String> deleteThisFile(@PathVariable("userid") long userid,
			@PathVariable("fileid") long fileid) {
		
		//delete file from storage and delete the entry from db
		boolean flag = storageService.deleteFile(userid, fileid);
		System.out.println(userid+" "+fileid);
		Map<String, String> response = new HashMap<String, String>();
		
		//setting response according to the logic 
		if (flag == true) {
		
			response.put("status", "SUCCESS");
		
		} else {
		
			response.put("status", "ERROR");
		
		}

		return response;
	
	}


	/**
	 * @Description  controller for getting user activity throughout the application
	 * 
	 * @Author Mritunjay Yadav
	 * @return List of TransactionManagementStuff objects
	 * @param userid 
	 * @Exception 
	 * 
	 * */
	@GetMapping("/activity/{userid}")
	@ResponseBody
	public List<TransactionManagementStuff> fetchUserActivity(@PathVariable("userid") long userid) {

		return viewShareDownloadService.getAllByuserid(userid);
	
	}
	

	/**
	 * @Description  controller for finding Document history
	 * 
	 * @Author Mritunjay Yadav
	 * @return List of String
	 * @param fileid
	 * @Exception 
	 * 
	 * */
	@GetMapping("/history/{fileid}")
	@ResponseBody
	public List<String> documentHistory(@PathVariable long fileid){
		
		return null;
	
	}
	

	/**
	 * @Description controller for deleting file Versions
	 * 
	 * @Author Mritunjay Yadav
	 * @return Map<String,String>
	 * @param userid and versionname
	 * @Exception 
	 * 
	 * */
	@GetMapping("/deleteVersion/{userid}/{versioname}")
	@ResponseBody
	public Map<String,String> deleteFileVersion(@PathVariable long userid, @PathVariable String versioname){
		
		return viewShareDownloadService.deleteVersionOfFile(userid, versioname);
	
	}

}
