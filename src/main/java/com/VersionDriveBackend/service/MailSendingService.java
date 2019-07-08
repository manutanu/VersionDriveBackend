/*
* MailSendingService
* This Interface Contains method signature for sending emails to users 
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import com.sun.mail.util.MailConnectException;

public interface MailSendingService {

	
	public Boolean sendMail(String to, String subject, String body) throws MailConnectException ;
}
