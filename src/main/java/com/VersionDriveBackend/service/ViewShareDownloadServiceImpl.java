/*
* ViewShareDownloadServiceImpl
* This Class Contains method implementations for View ,download , delete files
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.dto.ResponseFileObject;
import com.VersionDriveBackend.dto.ResponseSharedFileVO;
import com.VersionDriveBackend.dto.ShareRequest;
import com.VersionDriveBackend.entity.FileStuff;
import com.VersionDriveBackend.entity.Share;
import com.VersionDriveBackend.entity.TransactionManagementStuff;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.entity.VersionStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.ShareRepository;
import com.VersionDriveBackend.repository.TransactionRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.repository.VersionRepository;

@Service
public class ViewShareDownloadServiceImpl implements ViewShareDownloadService,ConstantUtils {

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

	@Autowired
	private MailSendingService mailSendingService;
	
	@Autowired
	private StorageUtilService storageUtilServie;

	
	/**
	 * @Description This method fetch all files in the drive of the user in sorted ordeer of date of insertion latest to be the first 
	 * 
	 * @Author Mritunjay Yadav
	 * @return List of ResponseFileObject 
	 * @param String userid 
	 * @Exception 
	 * 
	 */
	public List<ResponseFileObject> getAllFilesInSortedOrderOfInsertion(long userid) {
		
		//initialize response list and fetch userobject using userid
		List<ResponseFileObject> fileNames = new ArrayList<>();
		UserStuff userob = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);
		
		//removing backreferences from the objects to avoid recursive datafetch
		for (int i = 0; i < userob.getFileList().size(); i++) {
			
			userob.getFileList().get(i).setUser(null);
			
			for (Share ss : userob.getFileList().get(i).getSharelist()) {
			
				ss.setFileshare(null);
			
			}
			
			for (VersionStuff vv : userob.getFileList().get(i).getVersionlist()) {
				
				vv.setFileversion(null);
				vv.setUser(null);
			
			}
		
		}

		//sorting of fileobjects on the basis of insertiondate in DSC
		Collections.sort(userob.getFileList(), (Object o1, Object o2) -> {
			
			FileStuff a1 = (FileStuff) o1;
			FileStuff a2 = (FileStuff) o2;
			
			if (a1.getCreationDate().before(a2.getCreationDate())) {
			
				return 1;
			
			} else if (!a1.getCreationDate().before(a2.getCreationDate())) {
				
				return -1;
			
			} else {
				
				return 0;
			
			}
		
		});

		//forming response file objects from fileobjects List 
		userob.getFileList().forEach(filestuff -> {
			
			SimpleDateFormat form = new SimpleDateFormat("dd-MM-YYYY");
			String dateString = form.format(filestuff.getCreationDate());
			System.out.println(dateString);
			ResponseFileObject responob = new ResponseFileObject(filestuff.getFileid(), filestuff.getFilename(),
					dateString, filestuff.getUpdationDate(), filestuff.getSharelist(), filestuff.getVersionlist());
			fileNames.add(responob);
		
		});

		return fileNames;
	
	}

	
	/**
	 * @Description get User Details using userid and activated flag 
	 * 
	 * @Author Mritunjay Yadav
	 * @return UserStuff
	 * @param userid , activated 
	 * @Exception 
	 * 
	 */
	public UserStuff getUserByUseridAndVerified(long userid, int activated) {
		
		return userRepository.getUserByUseridAndVerified(userid, activated);
	
	}

	
	/**
	 * @Description get File Object using fileid 
	 * 
	 * @Author Mritunjay Yadav
	 * @return filestuff 
	 * @param fileid 
	 * @Exception 
	 * 
	 */
	public FileStuff getFileByFileid(long fileid) {
		
		return fileRepository.getFileByFileid(fileid);
	
	}

	
	/**
	 * @Description get User object using email and verified flag 
	 * 
	 * @Author Mritunjay Yadav
	 * @return UserStuff
	 * @param String email, int activated 
	 * @Exception 
	 * 
	 */
	public UserStuff getUserByEmailAndVerified(String email, int activated) {

		return userRepository.getUserByEmailAndVerified(email, activated);
	
	}

	
	/**
	 * @Description method to insert transactions into the database 
	 * 
	 * @Author Mritunjay Yadav
	 * @return boolean 
	 * @param action,filename,toeamil,fromemail,userid
	 * @Exception 
	 * 
	 */
	public boolean insertTransaction(String action, String filename, String toemail, String fromemail, long userid) {
		
		boolean flag = false;
		
		try {
			
			TransactionManagementStuff transaction = new TransactionManagementStuff();
			transaction.setActionTaken(action);
			transaction.setFileName(filename);
			// transaction.setUser(userObject);
			transaction.setUserid(userid);
			transactionRepository.save(transaction);
			flag = true;
		
		} catch (Exception e) {
			
			e.printStackTrace();
		
		}
		
		return flag;
	
	}

	
	/**
	 * @Description Method to share file bw two users
	 * 
	 * @Author Mritunjay Yadav
	 * @return Map<String,String>
	 * @param ShareRequest request
	 * @Exception 
	 * 
	 */
	public Map<String, String> shareFileUtility(ShareRequest request) {
		
		Map<String, String> responsemap = new HashMap<>();
		
		try {
			
			//fetching userobj  of both the users using emial and userid and activated true
			System.out.println(request.toString());
			UserStuff usertobeshared = getUserByEmailAndVerified(request.getToemail(), ACTIVATED);
			UserStuff uesrwhoshared = getUserByUseridAndVerified(request.getFromuserid(), ACTIVATED);
			FileStuff filewhichisshared = fileRepository.getOne(request.getFileid());
			System.out.println(request.getFromuserid() + " " + usertobeshared.getUserid() + " " + request.getFileid());
			
			//if this share activity already present with same filename in the database then send response that already shared this file to this user
			if (shareRepository.getShareTransaction(request.getFromuserid(), usertobeshared.getUserid(),
			
					request.getFileid()) != null) {
				/*
				 * return new ResponseEntity<>("Already", HttpStatus.OK); return
				 * ResponseEntity.accepted().body("Already"); return new
				 * ResponseEntity(HttpStatus.CONFLICT);
				 */
				responsemap.put("status", "Already");
				return responsemap;
			
			}
			
			//insert this share activity into the database
			Share sharetransaction = new Share();
			sharetransaction.setFromid(uesrwhoshared.getUserid());
			sharetransaction.setToid(usertobeshared.getUserid());
			sharetransaction.setFileshare(filewhichisshared);
			sharetransaction.setPermission(request.getPermission());
			shareRepository.save(sharetransaction);
			responsemap.put("status", "SUCCESS");
			
			//insert activity into transaction_table
			insertTransaction("SHARE",filewhichisshared.getFilename() , usertobeshared.getEmail(), uesrwhoshared.getEmail(),uesrwhoshared.getUserid() );
			
			uesrwhoshared.setFileList(null);
			// transaction.set(uesrwhoshared.get());

			// sending email to the user
			String body = uesrwhoshared.getEmail() + " shared " + filewhichisshared.getFilename()
					+ " To You Please Login to VersionDrive.com to preview , download ... and other options the file";
			mailSendingService.sendMail(request.getToemail(), filewhichisshared.getFilename(), body);

		} catch (Exception e) {
			
			e.printStackTrace();
			responsemap.put("status", "ERROR");
		
		}
	
		return responsemap;
	
	}

	
	/**
	 * @Description utility for showing shared file inthe shared folder of the users
	 * 
	 * @Author Mritunjay Yadav
	 * @return List of ResponseSharedFileVO
	 * @param userid
	 * @Exception 
	 * 
	 */
	public List<ResponseSharedFileVO> utilityForShowingSharedFiles(long userid) {
		
		//fetch all the Objects of share transactions fromthe database with userid 
		List<ResponseSharedFileVO> listofsharedfilestothisuser = new ArrayList<>();
		List<Share> shareob = shareRepository.getShareByToid(userid);

		//removing backrefernces in the vos and setting ResponseShareFileVO objects using the share objects to send as response
		shareob.forEach(shareo -> {
			
			UserStuff fromuserobject = userRepository.getUserByUseridAndVerified(shareo.getFromid(), ACTIVATED);
			List<VersionStuff> versionList = shareo.getFileshare().getVersionlist();
			
			for (VersionStuff versiontemp : versionList) {
			
				versiontemp.setFileversion(null);
				versiontemp.setUser(null);
		
			}
			
			//making objects to send
			ResponseSharedFileVO responob = new ResponseSharedFileVO(shareo.getFileshare().getFileid(),
					shareo.getFileshare().getFilename(), shareo.getFileshare().getCreationDate(),
					shareo.getFileshare().getUpdationDate(), shareo.getFromid(), fromuserobject.getUsername(),
					fromuserobject.getEmail(), shareo.getPermission().toUpperCase(), versionList);
			listofsharedfilestothisuser.add(responob);
		
		});

		return listofsharedfilestothisuser;
	
	}

	
	/**
	 * @Description get list of all the User objects which are verified 
	 * 
	 * @Author Mritunjay Yadav
	 * @return List UserStuff
	 * @param acitvated flag
	 * @Exception 
	 * 
	 */
	public List<UserStuff> getAllUserByVerified(int activated) {
		
		return userRepository.getAllUserByVerified(activated);

	}

	
	/**
	 * @Description Get All transaction of the users using userid 
	 * 
	 * @Author Mritunjay Yadav
	 * @return List of TransactionManagementStuff objects 
	 * @param userid
	 * @Exception 
	 * 
	 */
	public List<TransactionManagementStuff> getAllByuserid(long userid) {
		
		return transactionRepository.getAllByuserid(userid);
	
	}
	
	
	/**
	 * @Description method for deleting Version of a file from the drive
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param Multipart file, userid ,newname of the file
	 * @Exception 
	 * 
	 */
//	@Transactional
	public Map<String, String> deleteVersionOfFile(long userid, String versionname) {
		
		System.out.println(versionname+"  "+userid);
		Map<String, String> responseMap = new HashMap<>();
		
		try {
			
			//fetch all objects from version table with versionname  and userid  
			List<VersionStuff> listOfVersion = versionRepository.findAllByVersionname(versionname, userid);
			
			for(int i=0;i<listOfVersion.size();i++) {
				
				System.out.println(listOfVersion.get(i).getVersionname()+"  "+listOfVersion.get(i).getFileversion());
			
			}
			
			//delete all the files in the list from the database 
			versionRepository.deleteAll(listOfVersion);
			
			//delete all the version from the storage 
			storageUtilServie.deleteFileVersion(userid,versionname);
			
			//insert this transaction into the transactino_table 
			insertTransaction("DELETED VERSION", versionname, null, null, userid);
			responseMap.put("status", "200");
		
		} catch (Exception e) {
			
			e.printStackTrace();
			responseMap.put("status", "400");
		
		}
	
		return responseMap;
	
	}

}
