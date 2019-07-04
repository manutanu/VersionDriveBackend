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
import com.VersionDriveBackend.model.FileStuff;
import com.VersionDriveBackend.model.Share;
import com.VersionDriveBackend.model.TransactionManagementStuff;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.model.VersionStuff;
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

	public List<ResponseFileObject> getAllFilesInSortedOrderOfInsertion(long userid) {
		List<ResponseFileObject> fileNames = new ArrayList<>();

		UserStuff userob = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);
		for (int i = 0; i < userob.getFileList().size(); i++) {
			userob.getFileList().get(i).setUser(null);
			for (Share ss : userob.getFileList().get(i).getSharelist()) {
				ss.setFileshare(null);
			}
			for (VersionStuff vv : userob.getFileList().get(i).getVersionlist()) {
				vv.setFileversion(null);
			}
		}

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

		/*
		 * fileNames.sort((a1,a2)->{
		 * 
		 * });
		 */

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

	public UserStuff getUserByUseridAndVerified(long userid, int activated) {
		return userRepository.getUserByUseridAndVerified(userid, activated);
	}

	public FileStuff getFileByFileid(long fileid) {
		return fileRepository.getFileByFileid(fileid);
	}

	public UserStuff getUserByEmailAndVerified(String email, int activated) {
		return userRepository.getUserByEmailAndVerified(email, activated);
	}

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

	public Map<String, String> shareFileUtility(ShareRequest request) {
		Map<String, String> responsemap = new HashMap<>();
		try {
			System.out.println(request.toString());
			UserStuff usertobeshared = getUserByEmailAndVerified(request.getToemail(), ACTIVATED);
			UserStuff uesrwhoshared = getUserByUseridAndVerified(request.getFromuserid(), ACTIVATED);
			FileStuff filewhichisshared = fileRepository.getOne(request.getFileid());
			System.out.println(request.getFromuserid() + " " + usertobeshared.getUserid() + " " + request.getFileid());
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
			Share sharetransaction = new Share();
			sharetransaction.setFromid(uesrwhoshared.getUserid());
			sharetransaction.setToid(usertobeshared.getUserid());
			sharetransaction.setFileshare(filewhichisshared);
			sharetransaction.setPermission(request.getPermission());
			shareRepository.save(sharetransaction);
			responsemap.put("status", "SUCCESS");
			TransactionManagementStuff transaction = new TransactionManagementStuff();
			transaction.setActionTaken("SHARE");
			transaction.setFileName(filewhichisshared.getFilename());
			transaction.setToemail(usertobeshared.getEmail());
			transaction.setFromemail(uesrwhoshared.getEmail());
			uesrwhoshared.setFileList(null);
			;
			// transaction.set(uesrwhoshared.get());
			transaction.setUserid(uesrwhoshared.getUserid());
			transactionRepository.save(transaction);

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

	public List<ResponseSharedFileVO> utilityForShowingSharedFiles(long userid) {
		List<ResponseSharedFileVO> listofsharedfilestothisuser = new ArrayList<>();
		List<Share> shareob = shareRepository.getShareByToid(userid);

		shareob.forEach(shareo -> {
			UserStuff fromuserobject = userRepository.getUserByUseridAndVerified(shareo.getFromid(), ACTIVATED);
			List<VersionStuff> versionList = shareo.getFileshare().getVersionlist();
			for (VersionStuff versiontemp : versionList) {
				versiontemp.setFileversion(null);
			}
			ResponseSharedFileVO responob = new ResponseSharedFileVO(shareo.getFileshare().getFileid(),
					shareo.getFileshare().getFilename(), shareo.getFileshare().getCreationDate(),
					shareo.getFileshare().getUpdationDate(), shareo.getFromid(), fromuserobject.getUsername(),
					fromuserobject.getEmail(), shareo.getPermission().toUpperCase(), versionList);
			listofsharedfilestothisuser.add(responob);
		});

		return listofsharedfilestothisuser;
	}

	public List<UserStuff> getAllUserByVerified(int activated) {
		return userRepository.getAllUserByVerified(activated);

	}

	public List<TransactionManagementStuff> getAllByuserid(long userid) {
		return transactionRepository.getAllByuserid(userid);
	}
}
