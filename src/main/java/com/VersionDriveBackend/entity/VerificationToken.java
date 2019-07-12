package com.VersionDriveBackend.entity;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Value;

import com.VersionDriveBackend.constants.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Token")
public class VerificationToken implements ConstantUtils{

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long tokenid;
	
	@Column(name="token")
	private String token;
	
	@OneToOne
	private UserStuff user;
	
	@Column(name="expiryDate")
	private Date expiryDate;

	public long getTokenid() {
		return tokenid;
	}

	public void setTokenid(long tokenid) {
		this.tokenid = tokenid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserStuff getUser() {
		return user;
	}

	public void setUser(UserStuff user) {
		this.user = user;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	
	
}
