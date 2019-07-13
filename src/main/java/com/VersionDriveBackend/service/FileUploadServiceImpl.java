/*
* FileUploadServiceImpl
* This Class is service impl of FileUploadService interface
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.entity.FileStuff;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.entity.VersionStuff;
import com.VersionDriveBackend.repository.FileRepository;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.repository.VersionRepository;

@Service
public class FileUploadServiceImpl implements FileUploadService, ConstantUtils {

	@Autowired
	private StorageUtilService storageService;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VersionRepository versionRepository;

	@Autowired
	private ViewShareDownloadServiceImpl viewShareDownloadServiceImpl;

	public static int counter = 0;


	/**
	 * @Description  FileUpload Utility method which uploads file in the users drive and save that file object in the database
	 * 
	 * @Author Mritunjay Yadav
	 * @return Map<String , String>
	 * @param MultipartFile , userid
	 * @Exception 
	 * 
	 * */
	public Map<String, String> uploadingNewFile(MultipartFile file, long userid) {
		
		Map<String, String> response = new HashMap<>();
		String message = "";
		
		try {
			
			String newname = "";
			message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			FileStuff fileob = new FileStuff();
			fileob.setFilename(file.getOriginalFilename());
			fileob.setUser(userRepository.getUserByUseridAndVerified(userid, ACTIVATED));
			fileob.setAtestVersion(1.0);

			if (!CollectionUtils.isEmpty(fileRepository.getFileByFilename(file.getOriginalFilename(), userid))) {
				
				newname = counter + "@" + file.getOriginalFilename();
				fileob.setFilename(newname);
				counter++;
			
			}
			
			fileRepository.save(fileob);
			storageService.store(file, userid, newname);
//			files.add(file.getOriginalFilename());
			viewShareDownloadServiceImpl.insertTransaction("UPLOAD", file.getOriginalFilename(), null, null, userid);
			response.put("status", "SUCCESS");
			response.put("message", message);
			return response;
		
		} catch (Exception e) {
			
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			response.put("status", "ERROR");
			response.put("message", message);
			return response;
		
		}
		
	}

	
	/**
	 * @Description  Method is utility method for uploading versions of file in database and storage
	 * 
	 * @Author Mritunjay Yadav
	 * @return VersionStuff
	 * @param Multipart file, userid , fileid
	 * @Exception none
	 * 
	 * */
	public VersionStuff uploadingVersionOfFile(MultipartFile file, long userid, long fileid) {
		
		String message = "";
		Map<String, String> response = new HashMap<>();

		try {
			
			//fetch UserDetails using userid and removing backreferences so that recursive fetch doesnt happen
			UserStuff userForVersion = userRepository.getUserByUseridAndVerified(userid, ACTIVATED);
			userForVersion.setFileList(null);
			
			//get original file object using fileid and incrementing version number for that file in database 
			FileStuff fileob = fileRepository.getOne(fileid);
			fileob.setAtestVersion(fileob.getAtestVersion() + 1);
			
			//setting name for the new version using main file name and latestversion present in the file object in database
			StringBuilder nameinreverse = new StringBuilder(fileob.getFilename());
			String nameinreverestring = nameinreverse.reverse().substring(nameinreverse.indexOf(".") + 1,
					nameinreverse.length());
			String ext = (new StringBuilder(nameinreverse.substring(0, nameinreverse.indexOf(".")))).reverse()
					.toString();
			StringBuilder sb = new StringBuilder(nameinreverestring);
			String newfileversionname = sb.reverse().toString() + "v" + fileob.getAtestVersion() + "." + ext;
			System.out.println(newfileversionname);
			
			//creating version  object to save the entry in database
			VersionStuff versionFile = new VersionStuff();
			versionFile.setVersionname(newfileversionname);
			versionFile.setFileversion(fileob);

			//now after storing object in database now save the file version in the storage
			storageService.storeVersion(file, fileob.getUser().getUserid(), newfileversionname);
			fileRepository.save(fileob);
			versionFile.setUser(userForVersion);
			versionRepository.save(versionFile);

			//creating message for successfull and error in saving logic
			message = "You successfully uploaded " + file.getOriginalFilename() + " with name " + newfileversionname
					+ "!";

			viewShareDownloadServiceImpl.insertTransaction("UPLOADVERSION", newfileversionname, null, null, userid);
			versionFile.setFileversion(null);
			response.put("status", "SUCCESS");

			return versionFile;
		
		} catch (Exception e) {
			
			//in case of any exception occurs in saving logic
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return null;
		
		}

	}
}
