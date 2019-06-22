package com.VersionDriveBackend.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name="Version")
public class VersionStuff {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="versionid")
	private long versionid;
	
	@Column(name="versionname")
	private String versionname;
	
	@CreatedDate
	@Column(name="creationDate")
	private Date creationDate;
	
	@LastModifiedDate
	@Column(name="updationDate")
	private Date updationDate;
	
	@ManyToOne
	private FileStuff fileversion;

	public long getVersionid() {
		return versionid;
	}

	public void setVersionid(long versionid) {
		this.versionid = versionid;
	}

	public String getVersionname() {
		return versionname;
	}

	public void setVersionname(String versionname) {
		this.versionname = versionname;
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

	public FileStuff getFileversion() {
		return fileversion;
	}

	public void setFileversion(FileStuff fileversion) {
		this.fileversion = fileversion;
	}

	
}
