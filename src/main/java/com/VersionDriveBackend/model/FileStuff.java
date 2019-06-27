package com.VersionDriveBackend.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name="File")
public class FileStuff {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="fileid")
	private long fileid;
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="creationDate")
	private Date creationDate;
	
	@Column(name="updationDate")
	private Date updationDate;
	
	@Column(name="latestVersion")
	private double atestVersion;
	
	@ManyToOne
	private UserStuff user;
	
	@OneToMany(mappedBy="fileshare" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
	private List<Share> sharelist;
	
	
	@OneToMany(mappedBy="fileversion" , fetch = FetchType.LAZY , cascade = {CascadeType.PERSIST,CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH})
	private List<VersionStuff> versionlist;
	
	@PrePersist
	  protected void onCreate() {
	    creationDate = new Date();
	  }

	  @PreUpdate
	  protected void onUpdate() {
	    updationDate = new Date();
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

	public UserStuff getUser() {
		return user;
	}

	public void setUser(UserStuff user) {
		this.user = user;
	}

	public List<Share> getSharelist() {
		return sharelist;
	}

	public void setSharelist(List<Share> sharelist) {
		this.sharelist = sharelist;
	}

	public List<VersionStuff> getVersionlist() {
		return versionlist;
	}

	public void setVersionlist(List<VersionStuff> versionlist) {
		this.versionlist = versionlist;
	}

	public double getAtestVersion() {
		return atestVersion;
	}

	public void setAtestVersion(double atestVersion) {
		this.atestVersion = atestVersion;
	}
	
	
	
}
