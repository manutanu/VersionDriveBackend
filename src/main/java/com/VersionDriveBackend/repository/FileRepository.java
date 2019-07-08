/*
* FileRepository
*  This Interface is a repository for storing File detaile objects
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

import com.VersionDriveBackend.entity.FileStuff;

@Repository
public interface FileRepository extends JpaRepository<FileStuff,Long>{

	public FileStuff getFileByFileid(long fileid);
	
	@Query(value="select * from file where filename=?1 AND user_userid=?2" , nativeQuery=true )
	public List<FileStuff> getFileByFilename(String filename,long userid);
	
	
}

