package com.VersionDriveBackend.model;

import java.util.Date;

public class ResponseFileObject {
	
	private long fileid;
	
	private String filename;
	
	private Date creationDate;
	
	private Date updationDate;

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

	public ResponseFileObject(long fileid, String filename, Date creationDate, Date updationDate) {
		super();
		this.fileid = fileid;
		this.filename = filename;
		this.creationDate = creationDate;
		this.updationDate = updationDate;
	}

	public ResponseFileObject() {
		super();
	}
	
	
	
}
