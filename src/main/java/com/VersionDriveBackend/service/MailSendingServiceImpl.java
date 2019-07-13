/*
* MailSendingServiceImpl
* This service Contains Logic to send emails to users 
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.sun.mail.util.MailConnectException;

@Service
public class MailSendingServiceImpl implements MailSendingService{

	@Autowired
	private JavaMailSender javaMailSender;

	
//	@Async
	/**
	 * @Description  utility method for sending mails to the email with specified body and subject
	 * 
	 * @Author Mritunjay Yadav
	 * @return Boolean
	 * @param String to , String subject, String body
	 * @Exception MailConnectException
	 * 
	 * */
	public Boolean sendMail(String to, String subject, String body) throws MailConnectException {
		
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(body);
		javaMailSender.send(simpleMailMessage);
		return true;
	
	}

}
