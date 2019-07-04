/*
* UserRepository
*  This Interface is a repository for storing User detailed objects
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.model.UserStuff;

@Repository
public interface UserRepository extends JpaRepository<UserStuff,Long>{

	
	public UserStuff getUserByUsernameAndVerified(String username,int verified);
	
	public UserStuff getUserByEmailAndVerified(String email,int verified);
	
	public UserStuff getUserByUseridAndVerified(long userid,int verified);
	
	public List<UserStuff> getAllUserByVerified(int verified);
	
}
