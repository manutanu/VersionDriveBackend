package com.VersionDriveBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.model.UserStuff;

@Repository
public interface UserRepository extends JpaRepository<UserStuff,Long>{

	
	public UserStuff getUserByUsername(String username);
	
}
