/*
* ShareRepository
*  This Interface is a repository for storing Share detailed objects
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.entity.Share;

@Repository
public interface ShareRepository extends JpaRepository<Share,Long>{

	List<Share> getShareByToid(long userid);
	
	@Query(value = "Select * from share where fromid=?1 And toid=?2 And fileshare_fileid=?3" ,nativeQuery = true)
	Share getShareTransaction(long fromid, long toid,long fileid);
	

}
