/*
* ShareRequest
*  This Class is a VO for Modelling JSON of share request
*
* 1.0
*
* @authored by Mritunjay Yadav
*/


package com.VersionDriveBackend.dto;


public class ShareRequest {
	private String toemail;
	private long fromuserid;
	private String permission;
	private long fileid;
	
	
	
	@Override
	public String toString() {
		return "ShareRequest [toemail=" + toemail + ", fromuserid=" + fromuserid + ", permission=" + permission
				+ ", fileid=" + fileid + "]";
	}

	public ShareRequest(String toemail, long fromuserid, String permission, long fileid) {
		super();
		this.toemail = toemail;
		this.fromuserid = fromuserid;
		this.permission = permission;
		this.fileid = fileid;
	}

	public ShareRequest() {}

	public String getToemail() {
		return toemail;
	}

	public void setToemail(String toemail) {
		this.toemail = toemail;
	}

	public long getFromuserid() {
		return fromuserid;
	}

	public void setFromuserid(long fromuserid) {
		this.fromuserid = fromuserid;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public long getFileid() {
		return fileid;
	}

	public void setFileid(long fileid) {
		this.fileid = fileid;
	}
	
	
	
	
}
