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
@CrossOrigin({ "http://localhost:4100", "http://localhost:4200","http://192.168.43.195:4200" })
public class FileViewDownloadController implements ConstantUtils {

	@Autowired
	private ViewShareDownloadService viewShareDownloadService;
	
	@Autowired
	private StorageUtilService storageService;


	/** controller for listing all files */
	@GetMapping("/getallfiles/{userid}")
	public ResponseEntity<List<ResponseFileObject>> getListFiles(Model model, @PathVariable long userid) {
		List<ResponseFileObject> fileNames = new ArrayList<>();
		fileNames=viewShareDownloadService.getAllFilesInSortedOrderOfInsertion(userid);
		return ResponseEntity.ok().body(fileNames);
	}

	// controller for getting files for preview purpose from fileid
	@RequestMapping("/view/{userid}/{fileid}")
	public void viewResource(HttpServletRequest request, HttpServletResponse response, @PathVariable long fileid,
			@PathVariable long userid) throws IOException {
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		FileStuff fileByFileid = viewShareDownloadService.getFileByFileid(fileid);
		File file = new File(ROOT_DIR + "/" + userid + "@" + userob.getUsername() + "/" + fileByFileid.getFilename());
		if (file.exists()) {
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				/* unknown mimetype so set the mimetype to application/octet-stream */
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			/*
			 * Here we have mentioned it to show as attachment
			 * response.setHeader("Content-Disposition", String.format("attachment;
			 * filename=\"" + file.getName() + "\""));
			 */

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}

	/** controller for getting Version of files for preview purpose from filename */
	@RequestMapping("/viewversion/{userid}/{filename}")
	public void viewVersionFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String filename,
			@PathVariable long userid) throws IOException {
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		File file = new File(ROOT_DIR + "/" + userid + "@" + userob.getUsername() + "/" + filename);
		if (file.exists()) {
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				/* unknown mimetype so set the mimetype to application/octet-stream */
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			/*
			 * Here we have mentioned it to show as attachment
			 * response.setHeader("Content-Disposition", String.format("attachment;
			 * filename=\"" + file.getName() + "\""));
			 */

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}

	/** controller for getting files for download purpose */
	@GetMapping("/download/{userid}/{fileid}")
	@ResponseBody
	public void downloadResource(HttpServletRequest request, HttpServletResponse response, @PathVariable long fileid,
			@PathVariable long userid) throws IOException {
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		FileStuff fileByFileid = viewShareDownloadService.getFileByFileid(fileid);
		File file = new File(ROOT_DIR + "/" + userid + "@" + userob.getUsername() + "/" + fileByFileid.getFilename());
		if (file.exists()) {
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

			/*
			 * Here we have mentioned it to show as attachment
			 * response.setHeader("Content-Disposition", String.format("attachment;
			 * filename=\"" + file.getName() + "\""));
			 */

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());
			userob.setFileList(null);
			viewShareDownloadService.insertTransaction("DOWNLOAD",fileByFileid.getFilename(),null,null,userob.getUserid());
			
		}

	}

	/** controller for getting file versions for download purpose using filename */
	@GetMapping("/downloadversion/{userid}/{filename}")
	@ResponseBody
	public void downloadFileVersions(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String filename, @PathVariable long userid) throws IOException {
		UserStuff userob = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		File file = new File(ROOT_DIR + "/" + userid + "@" + userob.getUsername() + "/" + filename);
		if (file.exists()) {
			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

			/*
			 * Here we have mentioned it to show as attachment
			 * response.setHeader("Content-Disposition", String.format("attachment;
			 * filename=\"" + file.getName() + "\""));
			 */

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());
			userob.setFileList(null);
			viewShareDownloadService.insertTransaction("DOWNLOAD",filename,null,null,userob.getUserid());
		}
	}

	/** controller for sharing files */
	@PostMapping("/share/{fileid}")
	@ResponseBody
	public Map<String, String> shareThisFile(@RequestBody ShareRequest request) {
		
		/*
		 * HttpHeaders headers=new HttpHeaders(); /* return new
		 * ResponseEntity(HttpStatus.ACCEPTED);
		 */
		
		return viewShareDownloadService.shareFileUtility(request);
	}

	/** controller for showing shared files */
	@GetMapping("/shared/{userid}")
	public ResponseEntity<List<ResponseSharedFileVO>> getSharedFilesOfUser(@PathVariable("userid") long userid) {
		List<ResponseSharedFileVO> listofsharedfilestothisuser = new ArrayList<>();
		listofsharedfilestothisuser=viewShareDownloadService.utilityForShowingSharedFiles(userid);
		return ResponseEntity.ok().body(listofsharedfilestothisuser);
	}

	/** getting all user details for creating typeaheads */
	@GetMapping("/getallUserdetails")
	public ResponseEntity<List<String>> getAllUserDetails() {
		List<UserStuff> listofuser = viewShareDownloadService.getAllUserByVerified(ACTIVATED);
		List<String> emaillist = new ArrayList<>();
		for (int i = 0; i < listofuser.size(); i++) {
			emaillist.add(listofuser.get(i).getEmail());
		}
		return ResponseEntity.ok().body(emaillist);
	}

	/** controller for getting userdetail for particular user */
	@GetMapping("/getUserProfile/{userid}")
	@ResponseBody
	public UserResponseObject getUserProfileDetails(@PathVariable long userid) {
		UserStuff temp = viewShareDownloadService.getUserByUseridAndVerified(userid, ACTIVATED);
		UserResponseObject userStuff = new UserResponseObject();
		userStuff.setEmail(temp.getEmail());
		userStuff.setUsername(temp.getUsername());

		return userStuff;
	}

	/** controller for deleting file */
	@GetMapping("/deletefile/{userid}/{fileid}")
	public Map<String, String> deleteThisFile(@PathVariable("userid") long userid,
			@PathVariable("fileid") long fileid) {
		boolean flag = storageService.deleteFile(userid, fileid);
		System.out.println(userid+" "+fileid);
		Map<String, String> response = new HashMap<String, String>();
		if (flag == true) {
			response.put("status", "SUCCESS");
		} else {
			response.put("status", "ERROR");
		}

		return response;
	}

	/** controller for finding all activities */
	@GetMapping("/activity/{userid}")
	@ResponseBody
	public List<TransactionManagementStuff> fetchUserActivity(@PathVariable("userid") long userid) {

		return viewShareDownloadService.getAllByuserid(userid);
	}
	
	/** controller for finding Document history */
	@GetMapping("/history/{fileid}")
	@ResponseBody
	public List<String> documentHistory(@PathVariable long fileid){
		return null;
	}
	
	/** controller for deleting file Versions*/
	@GetMapping("/deleteVersion/{userid}/{versioname}")
	@ResponseBody
	public Map<String,String> deleteFileVersion(@PathVariable long userid, @PathVariable String versioname){
		return viewShareDownloadService.deleteVersionOfFile(userid, versioname);
	}

}

/*
 * @GetMapping("/view/{userid}/{fileid}")
 * 
 * @ResponseBody public ResponseEntity<Resource> getFileForPreview(@PathVariable
 * long fileid, @PathVariable long userid) { FileStuff fileByFileid =
 * fileRepository.getFileByFileid(fileid); Resource file =
 * storageService.loadFile(fileByFileid.getFilename(), userid);
 *
 * return ResponseEntity.ok() .header(HttpHeaders.CONTENT_DISPOSITION,
 * "inline; filename=\"" + file.getFilename() + "\"").body(file);
 * 
 * 
 * 
 * }
 *
 */
