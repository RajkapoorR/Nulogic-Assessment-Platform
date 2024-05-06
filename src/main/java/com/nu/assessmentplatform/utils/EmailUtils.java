package com.nu.assessmentplatform.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {

	@Autowired
	private JavaMailSenderImpl javaMailSender;

	public void sendEmail(String toAddress, String subject, String html) throws MessagingException {
		String fromAddress = "demo@sample.com";
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
		helper.setTo("mahalakshmi.pattan@nulogic.io");
		helper.setSubject(subject);
		helper.setText(html, true);
		helper.setFrom(fromAddress);
		javaMailSender.send(message);
	}
}
