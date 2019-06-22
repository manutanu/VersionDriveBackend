package com.VersionDriveBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.model.Share;

@Repository
public interface ShareRepository extends JpaRepository<Share,Long>{

}
