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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.PrePersist;
import javax.persistence.Transient;
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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Service
public class RegistrationServiceImpl implements RegistrationService, ConstantUtils {

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

	@Value("${VERIFICATION_TOKEN_EXPIRATION}")
	private String expirationOfToken;

	
	/**
	 * @Description utility for verifying that verification token is valid and not expired 
	 * 
	 * @Author Mritunjay Yadav
	 * @return String
	 * @param String VerificationToken
	 * @Exception 
	 * 
	 */
	public String verificationUtility(String verificationToken) {

		try {
			
			//check verifcation token present or not 
			if (!StringUtils.isEmpty(verificationToken)) {
				
				//fetch the object from the database using token string and also fetch user object corresponding to verificationToken object
				VerificationToken tokenObj = verificationTokenRepository.findByToken(verificationToken);
				UserStuff user = tokenObj.getUser();
				Calendar cal = Calendar.getInstance();

				//check if token is expired or not 
				if ((tokenObj.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
					
					verificationTokenRepository.delete(tokenObj);
					userRepository.delete(user);
					return "bad User or User Verification Link is expired please register again";
				
				}
				
				//else send response verified !!
				user.setVerified(ACTIVATED);
				userRepository.save(user);
				return "User verification SuccessFull ! Please Login to your account ";
			
			} else {

				//send this when no verification token is present
				return " bad VerificationToken ";

			}
		
		} catch (Exception e) {
			
			
			return " bad VerificationToken ";
		
		}
	
	}

	/**
	 * @Description registration controller utility method which registers user ,
	 *              generate verification link and send it to the user email
	 * 
	 * @Author Mritunjay Yadav
	 * @return Map<Object, Object >
	 * @param UserStuff object
	 * @Exception
	 * 
	 */
	@Transactional
	public Map<Object, Object> registrationUtility(UserStuff user) {

		Map<Object, Object> responseMap = new HashMap<>();
		VerificationToken verificationToken = new VerificationToken();
		System.out.println(user.getPassword() + " " + user.getUsername() + " " + encoder.encode(user.getPassword()));

		// fetching details from registration call and encrypting user password
		user.setPassword(encoder.encode(user.getPassword()));

		// fetching all users from database with given username and email with active
		// flag true if exist then send message for existing email and username
		UserStuff usernamecheck = userRepository.getUserByUsernameAndVerified(user.getUsername(), ACTIVATED);
		UserStuff useremailcheck = userRepository.getUserByEmailAndVerified(user.getEmail(), ACTIVATED);

		// setting newly added user object with verified as flase and save it into db
		user.setVerified(NOT_ACTIVATED);
		userRepository.save(user);

		try {
			
			//if username and useremail is not already exist in the database 
			if (usernamecheck == null && useremailcheck == null) {

				/** Verification Token Generation Logic using UUID util class */
				verificationToken = new VerificationToken();
				String tokenString = UUID.randomUUID().toString();
				verificationToken.setToken(tokenString);
				verificationToken.setUser(user);
				verificationToken.setExpiryDate(setExpiryDate());
				verificationTokenRepository.save(verificationToken);
				
				//message generation for verification mail sending logic 
				String body = "Please click on this link to complete verification ==> http://localhost:8080/verification/"
						+ tokenString;
				
				//mail sending using service MailSendingService
				mailSendingService.sendMail(user.getEmail(), "Verification Mail From VersionDrive.com", body);
				
				user.setRootfolder(user.getUserid() + "@" + user.getUsername());
				Path rootlocationdir = Paths.get(rootDir);
				boolean exists = Files.exists(rootlocationdir);
				
				//if rootdir already created
				if (exists) {
					
					Path userDrivePath = Paths.get(rootDir + "/" + user.getRootfolder());
					
					if (!Files.exists(userDrivePath)) {
						
						Files.createDirectory(userDrivePath);
					
					}
				
				//else create root dir with user specific drive
				} else {
					
					Files.createDirectory(rootlocationdir);
					Path userDrivePath = Paths.get(rootDir + "/" + user.getRootfolder());
					Files.createDirectory(userDrivePath);
				
				}

				//forming response for sending 
				responseMap.put("status", "SUCCESS");
				responseMap.put("userid", user.getUserid());
			
			} else if (usernamecheck != null) {
				
				//if username already exists in the database so delete just saved user in the database and send the response message
				userRepository.delete(user);
				verificationTokenRepository.delete(verificationToken);
				responseMap.put("status", "Username");
				responseMap.put("userid", -1);
			
			} else if (useremailcheck != null) {
				
				//if useremail already exists in the database so delete just saved user in the database and send the response message
				userRepository.delete(user);
				verificationTokenRepository.delete(verificationToken);
				responseMap.put("status", "Useremail");
				responseMap.put("userid", -1);
			
			}
		
		} catch (MailSendException ex) {
			
			//throw exception message if mail couldnt sent through
			ex.printStackTrace();
			userRepository.delete(user);
			verificationTokenRepository.delete(verificationToken);
			responseMap.put("status", "ADDRESS");
		
		} catch (MailParseException ex) {
			
			//throw exception message if mail is not right 
			userRepository.delete(user);
			verificationTokenRepository.delete(verificationToken);
			responseMap.put("status", "ADDRESS");
		
		} catch (Exception e) {
			
			//general exception
			e.printStackTrace();
			userRepository.delete(user);
			verificationTokenRepository.delete(verificationToken);
			responseMap.put("status", "ERROR");
		
		}
		
		//response map send
		return responseMap;
	
	}

	
	/**
	 * @Description utility method for setting expiry date for verificationToken
	 *              generated for the user after registration
	 * 
	 * @Author Mritunjay Yadav
	 * @return Date
	 * @param
	 * @Exception
	 * 
	 */
	public Date setExpiryDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		System.out.println(expirationOfToken + " hserer ");
		cal.add(Calendar.MINUTE, Integer.parseInt(expirationOfToken));
		return new Date(cal.getTime().getTime());
	}

}
