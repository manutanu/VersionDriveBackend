package com.VersionDriveBackend.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@OneToMany(mappedBy="user")
	private List<FileStuff> fileList;

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
	
		
}
