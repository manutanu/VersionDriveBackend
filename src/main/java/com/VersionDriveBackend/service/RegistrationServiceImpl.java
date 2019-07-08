/*
* RegistrationServiceImpl
* This class Contains  method implementation for registration controller
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.repository.UserRepository;

@Service
public class RegistrationServiceImpl implements RegistrationService,ConstantUtils{

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailSendingService mailSendingService;

	public String verificationUtility(String username) {
		StringBuilder sb = new StringBuilder("");

		byte[] bb = Base64.decodeBase64(username);
		for (int i = 0; i < bb.length; i++) {
			sb.append((char) (bb[i]) + "");
		}
		System.out.println(sb.toString());
		UserStuff users = userRepository.getUserByUsernameAndVerified(sb.toString(), NOT_ACTIVATED);
		Date currentDate = new Date();
		Date registrationDate = users.getCreationDate();
		Calendar expiryDate = Calendar.getInstance();
		expiryDate.setTime(registrationDate);
		expiryDate.add(Calendar.HOUR, 1);
		Date newexpiryDate = expiryDate.getTime();
		System.out.println(newexpiryDate+"  "+currentDate);
		if (newexpiryDate.before(currentDate)) {
			return "Link is expired";
		}
		users.setVerified(1);
		userRepository.save(users);
		return "SUCCESS";
	}
	
	@Transactional
	public Map<Object,Object> registrationUtility(UserStuff user){
		Map<Object, Object> responseMap = new HashMap<>();
		System.out.println(user.getPassword() + " " + user.getUsername() + " " + encoder.encode(user.getPassword()));
		
		user.setPassword(encoder.encode(user.getPassword()));
		
		UserStuff usernamecheck = userRepository.getUserByUsernameAndVerified(user.getUsername(), ACTIVATED);
		UserStuff useremailcheck = userRepository.getUserByEmailAndVerified(user.getEmail(), ACTIVATED);
		user.setVerified(NOT_ACTIVATED);
		userRepository.save(user);
		try {
			if (usernamecheck == null && useremailcheck == null) {
//				userRepository.save(user);
				
				//long userid = userRepository.getUserByUsernameAndVerified(user.getUsername(), NOT_ACTIVATED).getUserid();
				// sending email to the user
				String body = "Please click on this link to complete verification ==> http://localhost:8080/verification/"
						+ Base64.encodeBase64URLSafeString(user.getUsername().getBytes());
				mailSendingService.sendMail(user.getEmail(), "Verification Mail From VersionDrive.com", body);
				user.setRootfolder(user.getUserid() + "@" + user.getUsername());
				Path rootlocationdir = Paths.get(ROOT_DIR);
				boolean exists = Files.exists(rootlocationdir);
				if (exists) {
					Path userDrivePath = Paths.get(ROOT_DIR + "/" + user.getRootfolder());
					if(!Files.exists(userDrivePath)) {
						Files.createDirectory(userDrivePath);
					}
				} else {
					Files.createDirectory(rootlocationdir);
					Path userDrivePath = Paths.get(ROOT_DIR + "/" + user.getRootfolder());
					Files.createDirectory(userDrivePath);
				}
				
				responseMap.put("status", "SUCCESS");
				responseMap.put("userid", user.getUserid());
			} else if (usernamecheck != null) {
				userRepository.delete(user);
				responseMap.put("status", "Username");
				responseMap.put("userid", -1);
			} else if (useremailcheck != null) {
				userRepository.delete(user);
				responseMap.put("status", "Useremail");
				responseMap.put("userid", -1);
			}
		}catch(MailSendException ex) {
			userRepository.delete(user);
			responseMap.put("status","ADDRESS");
		}catch(MailParseException ex) {
			userRepository.delete(user);
			responseMap.put("status","ADDRESS");
		} catch (Exception e) {
			e.printStackTrace();
			userRepository.delete(user);
			responseMap.put("status", "ERROR");
		}
		return responseMap;
	}

}
