/*
* RegistrationController
* This Class is controller for all apis related to registration and verification of Users
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.service.MailSendingService;

@RestController
@CrossOrigin({ "http://localhost:4100", "http://localhost:4200" })
public class RegistrationController implements ConstantUtils {

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailSendingService mailSendingService;

	@PostMapping("/register")
	public Map<Object, Object> registerUser(@RequestBody UserStuff user) {
		System.out.println(user.getPassword() + " " + user.getUsername() + " " + encoder.encode(user.getPassword()));
		user.setPassword(encoder.encode(user.getPassword()));
		Map<Object, Object> responseMap = new HashMap<>();
		UserStuff usernamecheck = userRepository.getUserByUsernameAndVerified(user.getUsername(), ACTIVATED);
		UserStuff useremailcheck = userRepository.getUserByEmailAndVerified(user.getEmail(), ACTIVATED);
		user.setVerified(NOT_ACTIVATED);
		userRepository.save(user);
		try {
			if (usernamecheck == null && useremailcheck == null) {
//				userRepository.save(user);
				user.setRootfolder(user.getUserid() + "@" + user.getUsername());
				Path rootlocationdir = Paths.get(ROOT_DIR);
				boolean exists = Files.exists(rootlocationdir);
				if (exists) {
					Path userDrivePath = Paths.get(ROOT_DIR + "/" + user.getRootfolder());
					Files.createDirectory(userDrivePath);
				} else {
					Files.createDirectory(rootlocationdir);
					Path userDrivePath = Paths.get(ROOT_DIR + "/" + user.getRootfolder());
					Files.createDirectory(userDrivePath);
				}
				long userid = userRepository.getUserByUsernameAndVerified(user.getUsername(), NOT_ACTIVATED).getUserid();
				// sending email to the user
				String body = "Please click on this link to complete verification ==> http://localhost:8080/verification/"
						+ Base64.encodeBase64URLSafeString(user.getUsername().getBytes());
				mailSendingService.sendMail(user.getEmail(), "Verification Mail From VersionDrive.com", body);
				responseMap.put("status", "SUCCESS");
				responseMap.put("userid", userid);
			} else if (usernamecheck != null) {
				responseMap.put("status", "Username");
				responseMap.put("userid", -1);
			} else if (useremailcheck != null) {
				responseMap.put("status", "Useremail");
				responseMap.put("userid", -1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("status", "ERROR");
		}
		return responseMap;
	}

	/* Controller for verifying the user */
	@GetMapping("/verification/{username}")
	public String verifyUser(@PathVariable String username) {
		StringBuilder sb=new StringBuilder("");
		
		byte[] bb=Base64.decodeBase64(username);
		for(int i=0;i<bb.length;i++) {
			sb.append((char)(bb[i])+"");
		}
		System.out.println(sb.toString());
		UserStuff users = userRepository.getUserByUsernameAndVerified(sb.toString(), NOT_ACTIVATED);
		users.setVerified(1);
		userRepository.save(users);
		return "SUCCESS";
	}

}
