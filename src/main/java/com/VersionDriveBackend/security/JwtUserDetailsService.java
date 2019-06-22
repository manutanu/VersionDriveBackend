package com.VersionDriveBackend.security;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.VersionDriveBackend.model.UserStuff;
import com.VersionDriveBackend.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private PasswordEncoder obj;

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		

		UserStuff userstuff = userRepository.getUserByUsername(username);

		if (userstuff.getUsername().equals(username)) {
			return new User(userstuff.getUsername(), userstuff.getPassword(), new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		 

	}
	/*
	 * Optional<User> optionaluser=userDataRepository.findByUsername(username);
	 * System.out.println(optionaluser.get().getUser_password());
	 * optionaluser.orElseThrow(() -> new UsernameNotFoundException(username +
	 * " this user is not found !"));
	 * 
	 * CustomUserDetails user=optionaluser.map(users -> { return new
	 * CustomUserDetails(users); }).get();
	 * 
	 * //user=new CustomUserDetails(optionaluser.get());
	 * 
	 * return user;
	 */

}