package com.VersionDriveBackend.model;

import java.util.Date;
import java.util.List;

public class ResponseSharedFileVO {
	
	private long fileid;
	
	private String filename;
	
	private Date creationDate;
	
	private Date updationDate;
	
	private long ownerid;
	
	private String ownername;
	
	private String owneremail;
	
	private String permission;
	
	private List<VersionStuff> listOfVersionsOfSharedFiles; 
	

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public String getOwneremail() {
		return owneremail;
	}

	public void setOwneremail(String owneremail) {
		this.owneremail = owneremail;
	}

	public ResponseSharedFileVO() {
		super();
	}

	public long getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(long ownerid) {
		this.ownerid = ownerid;
	}

	public long getFileid() {
		return fileid;
	}

	public void setFileid(long fileid) {
		this.fileid = fileid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdationDate() {
		return updationDate;
	}

	public void setUpdationDate(Date updationDate) {
		this.updationDate = updationDate;
	}

	public List<VersionStuff> getListOfVersionsOfSharedFiles() {
		return listOfVersionsOfSharedFiles;
	}

	public void setListOfVersionsOfSharedFiles(List<VersionStuff> listOfVersionsOfSharedFiles) {
		this.listOfVersionsOfSharedFiles = listOfVersionsOfSharedFiles;
	}

	public ResponseSharedFileVO(long fileid, String filename, Date creationDate, Date updationDate, long ownerid,
			String ownername, String owneremail, String permission, List<VersionStuff> listOfVersionsOfSharedFiles) {
		super();
		this.fileid = fileid;
		this.filename = filename;
		this.creationDate = creationDate;
		this.updationDate = updationDate;
		this.ownerid = ownerid;
		this.ownername = ownername;
		this.owneremail = owneremail;
		this.permission = permission;
		this.listOfVersionsOfSharedFiles = listOfVersionsOfSharedFiles;
	}
	
	
	
}
