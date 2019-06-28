package com.VersionDriveBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.model.FileStuff;

@Repository
public interface FileRepository extends JpaRepository<FileStuff,Long>{

	public FileStuff getFileByFileid(long fileid);
	public List<FileStuff> getFileByFilename(String filename);
	
	
}

