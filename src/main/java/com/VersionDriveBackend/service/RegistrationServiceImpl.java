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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.entity.VerificationToken;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.repository.VerificationTokenRepository;

@Service
public class RegistrationServiceImpl implements RegistrationService,ConstantUtils{

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailSendingService mailSendingService;
	
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	
	@Value("${ROOT_DIR}")
	private String rootDir;

	public String verificationUtility(String verificationToken) {
		
		try {
		if(!StringUtils.isEmpty(verificationToken)) {
			VerificationToken tokenObj=verificationTokenRepository.findByToken(verificationToken);
			UserStuff user=tokenObj.getUser();
			Calendar cal = Calendar.getInstance();
			
			if ((tokenObj.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
				verificationTokenRepository.delete(tokenObj);
				userRepository.delete(user);
		        return "bad User or User Verification Link is expired please register again";
		    }
			user.setVerified(ACTIVATED);
			userRepository.save(user);
			return "User verification SuccessFull ! Please Login to your account ";
		}else {
			
			return " bad VerificationToken ";
			
		}
		}catch(Exception e) {
			return " bad VerificationToken ";
		}
	}
/*	StringBuilder sb = new StringBuilder("");
*		byte[] bb = Base64.decodeBase64(username);
*		for (int i = 0; i < bb.length; i++) {
*			sb.append((char) (bb[i]) + "");
*		}
*		System.out.println(sb.toString());
*		UserStuff users = userRepository.getUserByUsernameAndVerified(sb.toString(), NOT_ACTIVATED);
*		Date currentDate = new Date();
*		Date registrationDate = users.getCreationDate();
*		Calendar expiryDate = Calendar.getInstance();
*		expiryDate.setTime(registrationDate);
*		expiryDate.add(Calendar.HOUR, 1);
*		Date newexpiryDate = expiryDate.getTime();
*		System.out.println(newexpiryDate+"  "+currentDate);
*		if (newexpiryDate.before(currentDate)) {
*			return "Link is expired";
*		}
*		users.setVerified(1);
*	
		userRepository.save(users);
*/
	
	@Transactional
	public Map<Object,Object> registrationUtility(UserStuff user){
		Map<Object, Object> responseMap = new HashMap<>();
		VerificationToken verificationToken=new VerificationToken();
		System.out.println(user.getPassword() + " " + user.getUsername() + " " + encoder.encode(user.getPassword()));
		
		user.setPassword(encoder.encode(user.getPassword()));
		
		UserStuff usernamecheck = userRepository.getUserByUsernameAndVerified(user.getUsername(), ACTIVATED);
		UserStuff useremailcheck = userRepository.getUserByEmailAndVerified(user.getEmail(), ACTIVATED);
		user.setVerified(NOT_ACTIVATED);
		userRepository.save(user);
		try {
			if (usernamecheck == null && useremailcheck == null) {
//				userRepository.save(user);
				
				/** Verification Token Generation Logic using UUID util class*/
				 verificationToken=new VerificationToken();
				 String tokenString = UUID.randomUUID().toString();
				 verificationToken.setToken(tokenString);
				 verificationToken.setUser(user);
				verificationTokenRepository.save(verificationToken);
				
				String body = "Please click on this link to complete verification ==> http://localhost:8080/verification/"
						+ tokenString;
				mailSendingService.sendMail(user.getEmail(), "Verification Mail From VersionDrive.com", body);
				user.setRootfolder(user.getUserid() + "@" + user.getUsername());
				Path rootlocationdir = Paths.get(rootDir);
				boolean exists = Files.exists(rootlocationdir);
				if (exists) {
					Path userDrivePath = Paths.get(rootDir + "/" + user.getRootfolder());
					if(!Files.exists(userDrivePath)) {
						Files.createDirectory(userDrivePath);
					}
				} else {
					Files.createDirectory(rootlocationdir);
					Path userDrivePath = Paths.get(rootDir + "/" + user.getRootfolder());
					Files.createDirectory(userDrivePath);
				}
				
				responseMap.put("status", "SUCCESS");
				responseMap.put("userid", user.getUserid());
			} else if (usernamecheck != null) {
				userRepository.delete(user);
				verificationTokenRepository.delete(verificationToken);
				responseMap.put("status", "Username");
				responseMap.put("userid", -1);
			} else if (useremailcheck != null) {
				userRepository.delete(user);
				verificationTokenRepository.delete(verificationToken);
				responseMap.put("status", "Useremail");
				responseMap.put("userid", -1);
			}
		}catch(MailSendException ex) {
			ex.printStackTrace();
			userRepository.delete(user);
			verificationTokenRepository.delete(verificationToken);
			responseMap.put("status","ADDRESS");
		}catch(MailParseException ex) {
			userRepository.delete(user);
			verificationTokenRepository.delete(verificationToken);
			responseMap.put("status","ADDRESS");
		} catch (Exception e) {
			e.printStackTrace();
			userRepository.delete(user);
			verificationTokenRepository.delete(verificationToken);
			responseMap.put("status", "ERROR");
		}
		return responseMap;
	}

}
