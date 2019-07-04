/*
* MailSendingService
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

@Service
public class MailSendingService {
	
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	public boolean sendMail(String to, String subject , String body) {
		try {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(body);
		javaMailSender.send(simpleMailMessage);
		return true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
