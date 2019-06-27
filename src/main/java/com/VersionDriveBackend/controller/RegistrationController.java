package com.VersionDriveBackend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.repository.UserRepository;

@RestController
@CrossOrigin({"http://localhost:4100","http://localhost:4200"})
public class RegistrationController implements ConstantUtils{

	@Autowired
	private PasswordEncoder obj;
	
	@Autowired
	private UserRepository userRepository;
	
	
	//private final Path rootLocation = Paths.get("uploads");
	
	@PostMapping("/register")
	public Map<Object,Object> registerUser(@RequestBody UserStuff user){
		System.out.println(user.getPassword()+" "+user.getUsername()+" "+obj.encode(user.getPassword()));
		user.setPassword(obj.encode(user.getPassword()));
		Map<Object,Object> responseMap=new HashMap<>();
		UserStuff usernamecheck=userRepository.getUserByUsername(user.getUsername());
		UserStuff useremailcheck=userRepository.getUserByEmail(user.getEmail());
		try {
			if(usernamecheck==null && useremailcheck==null) {
				userRepository.save(user);
				user.setRootfolder(user.getUserid()+"@"+user.getUsername());
				userRepository.save(user);
				Path rootlocationdir=Paths.get(ROOT_DIR);
				boolean exists = Files.exists(rootlocationdir);
				if(exists) {
					Path userDrivePath = Paths.get(ROOT_DIR+"/"+user.getRootfolder());
					Files.createDirectory(userDrivePath);
				}else {
					Files.createDirectory(rootlocationdir);
					Path userDrivePath = Paths.get(ROOT_DIR+"/"+user.getRootfolder());
					Files.createDirectory(userDrivePath);
				}
				long userid=userRepository.getUserByUsername(user.getUsername()).getUserid();
				responseMap.put("status","SUCCESS");
				responseMap.put("userid",userid);
			}else if(usernamecheck!=null){
				responseMap.put("status","Username");
				responseMap.put("userid",-1);
			}else if(useremailcheck!=null) {
				responseMap.put("status","Useremail");
				responseMap.put("userid",-1);
			}
		}catch(Exception e) {
			e.printStackTrace();
			responseMap.put("status","ERROR");
		}
		return responseMap;
	}
	
}
 