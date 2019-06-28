package com.VersionDriveBackend.model;

import java.util.Date;
import java.util.List;

public class ResponseFileObject {
	
	private long fileid;
	
	private String filename;
	
	private String creationDate;
	
	private Date updationDate;
	
	private List<Share> shareList;
	
	private List<VersionStuff> versionList;

	
	
	public List<Share> getShareList() {
		return shareList;
	}

	public void setShareList(List<Share> shareList) {
		this.shareList = shareList;
	}

	public List<VersionStuff> getVersionList() {
		return versionList;
	}

	public void setVersionList(List<VersionStuff> versionList) {
		this.versionList = versionList;
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

	

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdationDate() {
		return updationDate;
	}

	public void setUpdationDate(Date updationDate) {
		this.updationDate = updationDate;
	}

	

	

	public ResponseFileObject(long fileid, String filename, String creationDate, Date updationDate,
			List<Share> shareList, List<VersionStuff> versionList) {
		super();
		this.fileid = fileid;
		this.filename = filename;
		this.creationDate = creationDate;
		this.updationDate = updationDate;
		this.shareList = shareList;
		this.versionList = versionList;
	}

	public ResponseFileObject() {
		super();
	}
	
	
	
}
