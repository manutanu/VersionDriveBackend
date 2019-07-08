/*
* UserStuff
*  This Class is an Entity for Storing User details in the database
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name="User")
public class UserStuff {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="userid")
	private long userid;
	
	@Column(name="username")
	private String username;
	
	@Column(name="password")
	private String password;
	
	@Column(name="rootfolder")
	private String rootfolder;
	
	@Column(name="creationdate")
	private Date creationDate;
	
	@Column(name="email")
	private String email;
	
	@Column(name="verified")
	private int verified;
	
	@OneToMany(mappedBy="user" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
	private List<FileStuff> fileList;
	
	@PrePersist
	public void persistCreationDate() {
		creationDate=new Date();
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRootfolder() {
		return rootfolder;
	}

	public void setRootfolder(String rootfolder) {
		this.rootfolder = rootfolder;
	}

	public List<FileStuff> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileStuff> fileList) {
		this.fileList = fileList;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getVerified() {
		return verified;
	}

	public void setVerified(int verified) {
		this.verified = verified;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	
	
		
}
