package com.VersionDriveBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.VersionDriveBackend.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{

	public VerificationToken findByToken(String verificationToken);

}
