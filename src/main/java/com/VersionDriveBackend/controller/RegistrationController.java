/*
* RegistrationController
* This Class is controller for all apis related to registration and verification of Users
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.entity.UserStuff;
import com.VersionDriveBackend.service.RegistrationService;

@RestController
@CrossOrigin({ "http://localhost:4100", "http://localhost:4200" ,"http://192.168.1.106:4200"})
public class RegistrationController implements ConstantUtils {
	
	@Autowired
	private RegistrationService registrationService;
	

	/**
	 * @Description  controller for registering user with the website
	 * 
	 * @Author Mritunjay Yadav
	 * @return Map<Object,Object>
	 * @param UserStuff object
	 * @Exception 
	 * 
	 * */
	@PostMapping("/register")
	public Map<Object, Object> registerUser(@RequestBody UserStuff user) {
		
		return registrationService.registrationUtility(user);
	
	}

	
	/**
	 * @Description  Controller for verifying the user 
	 * 
	 * @Author Mritunjay Yadav
	 * @return String
	 * @param String VerificationToken
	 * @Exception 
	 * 
	 * */
	@GetMapping("/verification/{verificationtoken}")
	public String verifyUser(@PathVariable String verificationtoken) {
	
		return registrationService.verificationUtility(verificationtoken);
	
	}

}
