package com.VersionDriveBackend.model;

public class ShareVersionRequest {
	
	private String toemail;
	private long fromuserid;
	private String permission;
	private long fileversionid;
	
	
	

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

	

	public long getFileversionid() {
		return fileversionid;
	}

	public void setFileversionid(long fileversionid) {
		this.fileversionid = fileversionid;
	}

	public ShareVersionRequest() {
		super();
	}

	public ShareVersionRequest(String toemail, long fromuserid, String permission, long fileversionid) {
		super();
		this.toemail = toemail;
		this.fromuserid = fromuserid;
		this.permission = permission;
		this.fileversionid = fileversionid;
	}

	
	
}
