/*
* VersionRepository
*  This Interface is a repository for storing Versions of file detailed objects
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

import com.VersionDriveBackend.entity.VersionStuff;




@Repository
public interface VersionRepository extends JpaRepository<VersionStuff,Long>{

	@Query(value="select * from version where versionname=?1 AND user_userid=?2" ,nativeQuery = true)
	List<VersionStuff> findAllByVersionname(String versionname,long userid);

}
