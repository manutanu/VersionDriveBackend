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

import com.VersionDriveBackend.constants.ConstantUtils;

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
	
	@PrePersist
	public void setExpiryDate() {
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EXPIRATION);
        expiryDate= new Date(cal.getTime().getTime());
	}

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
