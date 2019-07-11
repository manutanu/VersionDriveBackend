/*
* JwtAuthenticationController
*  This class contains api to authenticate users using jwt token authentication
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.dto.JwtRequest;
import com.VersionDriveBackend.dto.JwtResponse;
import com.VersionDriveBackend.repository.UserRepository;
import com.VersionDriveBackend.security.JwtTokenUtil;
import com.VersionDriveBackend.security.JwtUserDetailsService;


@RestController
@CrossOrigin({ "http://localhost:4100", "http://localhost:4200" ,"http://192.168.1.106:4200"})
public class JwtAuthenticationController implements ConstantUtils{

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	@Autowired
	private UserRepository userRepository;

	/**
	 * @Description  controller for authenticating user using jwt security module
	 * 
	 * @Author Mritunjay Yadav
	 * @return ResponseEntity of General type
	 * @param JWTRequest object 
	 * @Exception Exception
	 * 
	 * */
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		
		//call autheticate method to authenticate user is present or not in the database
		System.out.println(authenticationRequest.getUsername()+" "+authenticationRequest.getPassword());
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		
		//using loadUserByUsername method provided by spring security interface to authenticate user from db and generate JWT token
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		System.out.println("token is "+token);
		
		//fetching data for authenticated user to send userid and token in response 
		long userid=userRepository.getUserByUsernameAndVerified(authenticationRequest.getUsername(),ACTIVATED).getUserid();
		return ResponseEntity.ok(new JwtResponse(token,userid,authenticationRequest.getUsername()));
	
	}

	/**
	 * @Description  authenticate method used to authenticate user from database using username and password
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param username and password
	 * @Exception Exception
	 * 
	 * */
	private void authenticate(String username, String password) throws Exception {
		
		try {
			
			//authenticate user from database 
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		
		} catch (DisabledException e) {
			
			throw new Exception("USER_DISABLED", e);
		
		} catch (BadCredentialsException e) {
			
			throw new Exception("INVALID_CREDENTIALS", e);
		
		}
		
	}
}