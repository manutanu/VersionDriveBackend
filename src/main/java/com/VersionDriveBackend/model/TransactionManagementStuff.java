/*
* TransactionManagementStuff
*  This Class is an Entity for storing every activity that user make on dashboard
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name="TransactionTable")
public class TransactionManagementStuff {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="transId")
	private long transid;
	
	@Column(name="actionTaken")
	private String actionTaken;
	
	@Column(name="filename")
	private String fileName;
	
	@Column(name="fromemail")
	private String fromemail;
	
	@Column(name="toemail")
	private String toemail;

	@Column(name="userid")
	private long userid;
	
	@Column(name="creationDate")
	private Date creationDate;
	

	
	@PrePersist
	  protected void onCreate() {
		creationDate = new Date();
	  }

	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public long getTransid() {
		return transid;
	}

	public void setTransid(long transid) {
		this.transid = transid;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public TransactionManagementStuff() {
		super();
	}

	public String getFromemail() {
		return fromemail;
	}

	public void setFromemail(String fromemail) {
		this.fromemail = fromemail;
	}

	public String getToemail() {
		return toemail;
	}

	public void setToemail(String toemail) {
		this.toemail = toemail;
	}

	public TransactionManagementStuff(long transid, String actionTaken, String fileName, String fromemail,
			String toemail) {
		super();
		this.transid = transid;
		this.actionTaken = actionTaken;
		this.fileName = fileName;
		this.fromemail = fromemail;
		this.toemail = toemail;
	}
	
	
	
}
