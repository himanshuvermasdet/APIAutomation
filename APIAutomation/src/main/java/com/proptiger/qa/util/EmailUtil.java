package com.proptiger.qa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;

/**
 * User: Himanshu.Verma
 */

public class EmailUtil {

	/**
	 * This function is used to send the email.
	 * @param reportCompleteName : Complete path of the Report which needs to be send as attachment 
	 * @param receiver : Receiver of the email.
	 * @param sender : Sender of the email.
	 * @param password : Password of the sender.
	 * @param emailReportSubject : Subject of the email.
	 * @param emailReportBody : Email Body.
	 */
	public static void sendEmail(ArrayList<HashMap <String, HashMap <String, Integer>>> reportCompleteName, String receiver, final String sender,
			final String password, String emailReportSubject, String emailReportBody,File tmpFile) {

			sendEmailThroughGmailSmtp(reportCompleteName, receiver, sender, password, emailReportSubject, emailReportBody, tmpFile);
		
	}
	
	private static void sendEmailThroughGmailSmtp(ArrayList<HashMap <String, HashMap <String, Integer>>> reportCompleteName, String receiver, final String sender,
			final String password, String emailReportSubject, String emailReportBody,File tmpFile){

		String recipient = receiver;
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			message.setSubject(emailReportSubject);

			// creates message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(emailReportBody, "text/html");

			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			BodyPart messageBodyPart1 = new MimeBodyPart();
			
			String mess = "";
			try
			{
				mess = FileUtils.readFileToString(tmpFile);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			// adds attachments

			messageBodyPart1.setContent(mess,"text/html; charset=utf-8");
			multipart.addBodyPart(messageBodyPart1);
			
			MimeBodyPart attachPart = null;
			
			try {
				for (int i = 0; i < reportCompleteName.size(); i++) {
					for (String reportNameKey : reportCompleteName.get(i).keySet()){
						System.out.println("Result File to be attached ::" +reportNameKey);
						attachPart = new MimeBodyPart();
						attachPart.attachFile(reportNameKey);
						multipart.addBodyPart(attachPart);
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			message.setContent(multipart);
			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	
}
