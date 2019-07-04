/*
* EmailVO
* This Class is a VO for carrying data regarding sending email
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.dto;

public class EmailVO {
	
	private String toemail;
	
	private String fromemail;
	
	private String body;

	public String getToemail() {
		return toemail;
	}

	public void setToemail(String toemail) {
		this.toemail = toemail;
	}

	public String getFromemail() {
		return fromemail;
	}

	public void setFromemail(String fromemail) {
		this.fromemail = fromemail;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public EmailVO(String toemail, String fromemail, String body) {
		super();
		this.toemail = toemail;
		this.fromemail = fromemail;
		this.body = body;
	}

	public EmailVO() {
		super();
	}
	
	

}
