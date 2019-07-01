package com.VersionDriveBackend.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.VersionDriveBackend.model.ResponseSharedFileVO;
import com.VersionDriveBackend.model.Share;
import com.VersionDriveBackend.model.ShareRequest;
import com.VersionDriveBackend.model.TransactionManagementStuff;
import com.VersionDriveBackend.model.UserResponseObject;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.model.VersionStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.ShareRepository;
import com.VersionDriveBackend.repository.TransactionRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.repository.VersionRepository;
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

	@Autowired
	private VersionRepository versionRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;

	

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
		
		Collections.sort(userobject.getFileList(),new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				FileStuff a1=(FileStuff)o1;
				FileStuff a2=(FileStuff)o2;
				if(a1.getCreationDate().before(a2.getCreationDate())) {
					return 1;
				}else if(!a1.getCreationDate().before(a2.getCreationDate())) {
					return -1;
				}else {
					return 0;
				}
			}
		});
		
		userobject.getFileList().forEach(filestuff -> {
			SimpleDateFormat form=new SimpleDateFormat("dd-MM-YYYY");
			String dateString=form.format(filestuff.getCreationDate());
			System.out.println(dateString);
			ResponseFileObject responob=new ResponseFileObject(filestuff.getFileid(), filestuff.getFilename(), dateString, filestuff.getUpdationDate(),filestuff.getSharelist(),filestuff.getVersionlist());
			fileNames.add(responob);
		});
		
//		fileNames.sort((a1,a2)->{
//			
//		});
		return ResponseEntity.ok().body(fileNames);
	}
	
	
	// controller for getting files for preview purpose from fileid
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
	
	// controller for getting Version of files for preview purpose from filename
		@RequestMapping("/viewversion/{userid}/{filename}")
		public void viewVersionFile(HttpServletRequest request, HttpServletResponse response,
				@PathVariable String filename, @PathVariable long userid) throws IOException {
			UserStuff userObject = userRepository.getOne(userid);
			File file = new File(ROOT_DIR+"/"+userid+"@"+userObject.getUsername()+"/"+filename);
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
			TransactionManagementStuff transaction=new TransactionManagementStuff();
			transaction.setActionTaken("DOWNLOAD");
			transaction.setFileName(fileByFileid.getFilename());
			userObject.setFileList(null);
//			transaction.setUser(userObject);
			transaction.setUserid(userObject.getUserid());
			transactionRepository.save(transaction);
		}
		
	}
	
	
	// controller for getting file versions for download purpose using filename
		@GetMapping("/downloadversion/{userid}/{filename}")
		@ResponseBody
		public void downloadFileVersions(HttpServletRequest request, HttpServletResponse response,
				@PathVariable String filename, @PathVariable long userid) throws IOException {
			UserStuff userObject = userRepository.getOne(userid);
			File file = new File(ROOT_DIR+"/"+userid+"@"+userObject.getUsername()+"/"+filename);
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
				TransactionManagementStuff transaction=new TransactionManagementStuff();
				transaction.setActionTaken("DOWNLOAD");
				transaction.setFileName(filename);
				userObject.setFileList(null);
//				transaction.setUser(userObject);
				transaction.setUserid(userObject.getUserid());
				transactionRepository.save(transaction);
			}
		}
	
	
	
	
	// controller for sharing files
	@PostMapping("/share/{fileid}")
	@ResponseBody
	public Map<String,String> shareThisFile(@RequestBody ShareRequest request){
		Map<String,String> responsemap=new HashMap<>();
		try {
			System.out.println(request.toString());
			UserStuff usertobeshared=userRepository.getUserByEmail(request.getToemail());
			Optional<UserStuff> uesrwhoshared=userRepository.findById(request.getFromuserid());
			FileStuff filewhichisshared=fileRepository.getOne(request.getFileid());
			System.out.println(request.getFromuserid()+" "+usertobeshared.getUserid()+" "+request.getFileid());
			if(shareRepository.getShareTransaction(request.getFromuserid(), usertobeshared.getUserid(), request.getFileid())!=null) {
				//return  new ResponseEntity<>("Already", HttpStatus.OK);
				//return ResponseEntity.accepted().body("Already");
				//return new ResponseEntity(HttpStatus.CONFLICT);
				 responsemap.put("status","Already");
				return responsemap;
			}
			Share sharetransaction=new Share();
			sharetransaction.setFromid(uesrwhoshared.get().getUserid());
			sharetransaction.setToid(usertobeshared.getUserid());
			sharetransaction.setFileshare(filewhichisshared);
			sharetransaction.setPermission(request.getPermission());
			shareRepository.save(sharetransaction);
			responsemap.put("status","SUCCESS");
			TransactionManagementStuff transaction=new TransactionManagementStuff();
			transaction.setActionTaken("SHARE");
			transaction.setFileName(filewhichisshared.getFilename());
			transaction.setToemail(usertobeshared.getEmail());
			transaction.setFromemail(uesrwhoshared.get().getEmail());
			uesrwhoshared.get().setFileList(null);;
//			transaction.set(uesrwhoshared.get());
			transaction.setUserid(uesrwhoshared.get().getUserid());
			transactionRepository.save(transaction);
		}catch(Exception e) {
			e.printStackTrace();
			responsemap.put("status","ERROR");
		}
		//HttpHeaders headers=new HttpHeaders();
		//return new ResponseEntity(HttpStatus.ACCEPTED);
		return responsemap;
	}
	
	// controller for showing shared files
	@GetMapping("/shared/{userid}")
	public ResponseEntity<List<ResponseSharedFileVO>> getSharedFilesOfUser(@PathVariable("userid") long userid ){
		List<ResponseSharedFileVO> listofsharedfilestothisuser=new ArrayList<>();
		
		List<Share> shareobject=shareRepository.getShareByToid(userid);
		
		shareobject.forEach(shareo -> {
			UserStuff fromuserobject=userRepository.getOne(shareo.getFromid());
			List<VersionStuff> versionList=shareo.getFileshare().getVersionlist();
			for(VersionStuff versiontemp:versionList) {
				versiontemp.setFileversion(null);
			}
			ResponseSharedFileVO responob=new ResponseSharedFileVO(shareo.getFileshare().getFileid(), shareo.getFileshare().getFilename(), shareo.getFileshare().getCreationDate(), shareo.getFileshare().getUpdationDate(),shareo.getFromid(),fromuserobject.getUsername(),fromuserobject.getEmail(),shareo.getPermission().toUpperCase(),versionList);
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
	
	
	//controller for getting userdetail for particular user
	@GetMapping("/getUserProfile/{userid}")
	@ResponseBody
	public UserResponseObject getUserProfileDetails(@PathVariable long userid){
		UserStuff temp=userRepository.getOne(userid);
		UserResponseObject userStuff=new UserResponseObject();
		userStuff.setEmail(temp.getEmail());
		userStuff.setUsername(temp.getUsername());
		
		return userStuff;
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
	
	
	//controller for finding all activities
	@GetMapping("/activity/{userid}")
	@ResponseBody
	public List<TransactionManagementStuff> fetchUserActivity(@PathVariable("userid") long userid ){
		
		return transactionRepository.getAllByuserid(userid);
	}
	
	// controller for sharing version files
//		@PostMapping("/sharebyversionid/{fileversionid}")
//		@ResponseBody
//		public Map<String,String> shareThisFileVersion(@RequestBody ShareVersionRequest request){
//			Map<String,String> responsemap=new HashMap<>();
//			try {
//				System.out.println(request.toString());
//				UserStuff usertobeshared=userRepository.getUserByEmail(request.getToemail());
//				Optional<UserStuff> uesrwhoshared=userRepository.findById(request.getFromuserid());
//				VersionStuff fileversionwhichisshared=versionRepository.getOne(request.getFileversionid());
//				FileStuff filewhoseversiontobeshared=fileversionwhichisshared.getFileversion();
//				System.out.println(request.getFromuserid()+" "+usertobeshared.getUserid()+" "+request.getFileversionid());
//				if(shareRepository.getShareTransaction(request.getFromuserid(), usertobeshared.getUserid(), filewhoseversiontobeshared.getFileid())!=null) {
//					//return  new ResponseEntity<>("Already", HttpStatus.OK);
//					//return ResponseEntity.accepted().body("Already");
//					//return new ResponseEntity(HttpStatus.CONFLICT);
//					 responsemap.put("status","Already");
//					return responsemap;
//				}
//				Share sharetransaction=new Share();
//				sharetransaction.setFromid(uesrwhoshared.get().getUserid());
//				sharetransaction.setToid(usertobeshared.getUserid());
//				sharetransaction.setFileshare(filewhichisshared);
//				sharetransaction.setPermission(request.getPermission());
//				shareRepository.save(sharetransaction);
//				responsemap.put("status","SUCCESS");
//			}catch(Exception e) {
//				e.printStackTrace();
//				responsemap.put("status","ERROR");
//			}
//			//HttpHeaders headers=new HttpHeaders();
//			//return new ResponseEntity(HttpStatus.ACCEPTED);
//			return responsemap;
//		}
	

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
