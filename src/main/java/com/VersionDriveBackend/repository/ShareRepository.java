package com.VersionDriveBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.VersionDriveBackend.model.Share;

@Repository
public interface ShareRepository extends JpaRepository<Share,Long>{

	List<Share> getShareByToid(long userid);

}
