package com.scholastic.sbam.server.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.scholastic.sbam.server.database.codegen.User;


public class MailHelper {

	
	private static final String CLASS_NAME = new MailHelper().getClass().getName();
	 
	private static class SMTPAuthenticator extends javax.mail.Authenticator
	{
		private String username;
		private String password;
		public SMTPAuthenticator(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(username, password);
		}
	}

	public static void sendMail(String smtpServer, int smtpPort, String smtpuser, String password, String to, String cc, String bcc, String from, String subject, String body)
		throws Exception {
		
		logConsole("Send " + subject + " to " + to + " from " + from);

		Properties props = System.getProperties();
		
		Authenticator auth = null;
		
		// -- Attaching to default Session, or we could start a new one --
		if (smtpuser != null && smtpuser.length() > 0 && password != null && password.length() > 0) {
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.auth", "true");
			props.put("mail.smtps.host", smtpServer);	
			props.put("mail.smtps.user", smtpuser);
			props.put("mail.smtps.password", password);
			if (smtpPort != 0 && smtpPort != 25)
				props.put("mail.smtps.port", smtpPort + "");

			auth = new SMTPAuthenticator(smtpuser, password);
		} else {
			props.put("mail.smtp.host", smtpServer);
			if (smtpPort != 0 && smtpPort != 25)
				props.put("mail.smtp.port", smtpPort + "");
		}
		
		Session session = Session.getInstance(props, auth);
	//	session.setDebug(true);
		
		// -- Create a new message --
		Message msg = new MimeMessage(session);
		// -- Set the FROM and TO fields --
		msg.setFrom(new InternetAddress(from));
		msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to, false));
		if (cc != null && cc.length() > 0)
			msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(cc, false));
		if (bcc != null && bcc.length() > 0)
			msg.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(bcc, false));
		// -- Set the subject and body text --
		msg.setSubject(subject);
		if (body.substring(0,6).toLowerCase().equals("<html>"))
			msg.setContent(body, "text/html");
		else
			msg.setText(body);
		// -- Set some other header information --
		msg.setHeader("X-Mailer", "libraryep");
		msg.setSentDate(new Date());
	    msg.saveChanges();      // don't forget this
	    
		// -- Send the message --
		if (smtpuser != null && smtpuser.length() > 0) {
		    Transport tr = session.getTransport();
		    if (smtpPort != 0 && smtpPort != 25)
		    	tr.connect(smtpServer, smtpPort, smtpuser, password);
		    else
		    	tr.connect(smtpServer, smtpuser, password);
		    tr.sendMessage(msg, msg.getAllRecipients());
		    tr.close();
		} else 
			Transport.send(msg);
	}

	public static void sendMail(String smtpServer, int smtpPort, String smtpuser, String password, String to, String cc, String from, String subject, String body)
		throws Exception {
		sendMail(smtpServer, smtpPort, smtpuser, password, to, cc, null, from, subject, body);
	}

	public static void sendMail(String smtpServer, int smtpPort, String smtpuser, String password, String to, String from, String subject, String body)
		throws Exception {
		sendMail(smtpServer, smtpPort, smtpuser, password, null, null, from, subject, body);
	}
	
	public static void sendMail(String to, String from, String subject, String body) throws Exception {
		sendMail(AppServerConstants.getEmailServer(), AppServerConstants.getEmailPort(), AppServerConstants.getEmailUser(), AppServerConstants.getEmailPassword(), to, null, null, from, subject, body);
	}
	
	public static void sendMailFromFile(User to, String from, String subject, String bodyfile) throws Exception {
		sendMail(AppServerConstants.getEmailServer(), AppServerConstants.getEmailPort(), AppServerConstants.getEmailUser(), AppServerConstants.getEmailPassword(), to.getEmail(), null, null, from, subject, readTextFile(bodyfile, getReplacements(to)));
	}
	
	public static void sendMailFromFile(User to, String subject, String bodyfile) throws Exception {
		sendMail(AppServerConstants.getEmailServer(), AppServerConstants.getEmailPort(), AppServerConstants.getEmailUser(), AppServerConstants.getEmailPassword(), to.getEmail(), null, null, AppServerConstants.getEmailAddress(), subject, readTextFile(bodyfile, getReplacements(to)));
	}
	
	public static void sendStandardMail(String to, String from, String subject, String body) throws Exception {
		sendMail(AppServerConstants.getEmailServer(), AppServerConstants.getEmailPort(), AppServerConstants.getEmailUser(), AppServerConstants.getEmailPassword(), to, AppServerConstants.getEmailCc(), AppServerConstants.getEmailBcc(), from, subject, body);
	}
	
	public static void sendStandardMail(String to, String subject, String body) throws Exception {
		sendStandardMail(to, AppServerConstants.getEmailAddress(), subject, body);
	}
	
	public static void sendStandardMailFromFile(String to, String from, String subject, String bodyfile) throws Exception {
		sendStandardMail(to, from, subject, readTextFile(bodyfile, null));
	}
	
	public static void sendStandardMailFromFile(String to, String subject, String bodyfile) throws Exception {
		sendStandardMailFromFile(to, AppServerConstants.getEmailAddress(), subject, bodyfile);
	}
	
	public static void sendStandardMailFromFile(User to, String from, String subject, String bodyfile) throws Exception {
		sendStandardMailFromFile(to.getEmail(), from, subject, bodyfile, getReplacements(to));
	}
	
	public static void sendStandardMailFromFile(User to, String subject, String bodyfile) throws Exception {
		sendStandardMailFromFile(to.getEmail(), subject, bodyfile, getReplacements(to));
	}
	
//	public static void sendStandardMailFromFile(User to, String from, String subject, String bodyfile) throws Exception {
//		sendStandardMailFromFile(to.getEmail(), from, subject, bodyfile, getReplacements(to));
//	}
//	
//	public static void sendStandardMailFromFile(User to, String subject, String bodyfile) throws Exception {
//		sendStandardMailFromFile(to.getEmail(), subject, bodyfile, getReplacements(to));
//	}
	
	public static void sendStandardMailFromFile(String to, String from, String subject, String bodyfile, Map<String, String> replacements) throws Exception {
		sendStandardMail(to, from, subject, readTextFile(bodyfile, replacements));
	}
	
	public static void sendStandardMailFromFile(String to, String subject, String bodyfile, Map<String, String> replacements) throws Exception {
		sendStandardMailFromFile(to, AppServerConstants.getEmailAddress(), subject, bodyfile, replacements);
	}
	
//	public static void sendStandardMailFromFile(User to, String from, String subject, String bodyfile, Map<String, String> replacements) throws Exception {
//		addReplacements(to, replacements);
//		sendStandardMailFromFile(to.getEmail(), from, subject, bodyfile, replacements);
//	}
//	
//	public static void sendStandardMailFromFile(User to, String subject, String bodyfile, Map<String, String> replacements) throws Exception {
//		addReplacements(to, replacements);
//		sendStandardMailFromFile(to.getEmail(), subject, bodyfile, replacements);
//	}
	
	public static void sendStandardMailFromFile(User to, String from, String subject, String bodyfile, Map<String, String> replacements) throws Exception {
		addReplacements(to, replacements);
		sendStandardMailFromFile(to.getEmail(), from, subject, bodyfile, replacements);
	}
	
	public static void sendStandardMailFromFile(User to, String subject, String bodyfile, Map<String, String> replacements) throws Exception {
		addReplacements(to, replacements);
		sendStandardMailFromFile(to.getEmail(), subject, bodyfile, replacements);
	}
	
	/**
	 * Get a map of replacement values for a user.
	 * @param user
	 * @return
	 */
	public static Map<String, String> getReplacements(User user) {
		Map<String, String> replacements = new HashMap<String, String>();
		
		addReplacements(user, replacements);
		
		return replacements;
	}
	
	/**
	 * Add the replacement values for a user to a map of other values.
	 * @param user
	 * @param replacements
	 */
	public static void addReplacements(User user, Map<String, String> replacements) {
		replacements.put("username", user.getUserName());
		replacements.put("firstname", user.getFirstName());
		replacements.put("lastname", user.getLastName());
		replacements.put("email", user.getEmail());
		replacements.put("password", user.getPassword());
//		replacements.put("displayname", user.getDisplayName());
	}
	
	/**
	 * Read a text file and make replacements from a map of values.
	 * @param fullPathFilename
	 * @param replacements
	 * @return
	 * @throws IOException
	 */
	private static String readTextFile(String filename, Map<String, String> replacements) throws IOException {
		String fullPathFilename = AppServerConstants.getEmailFilesRoot() + filename;
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new FileReader(fullPathFilename));
				
		char[] chars = new char[1024];
		while( (reader.read(chars)) > -1){
			sb.append(String.valueOf(chars));	
		}

		reader.close();

		return doReplacements(sb, replacements);
	}
	
	/**
	 * Make replacements in a string buffer from a map of values.
	 * 
	 * Values to be replaced begin and end with the strings declared in SLI_REPLACEMENT_START and SLI_REPLACEMENT_END.
	 *
	 * Errors are NOT trapped.  Errors just result in an incorrectly formed e-mail.
	 * 
	 * @param sb
	 * @param replacements
	 * @return
	 */
	private static String doReplacements(StringBuffer sb, Map<String, String> replacements) {
		
		if (!replacements.containsKey("datetime"))
			replacements.put("datetime", new Date().toString());
		if (!replacements.containsKey("siteurl"))
			replacements.put("siteurl", AppServerConstants.getSiteUrl());
		if (!replacements.containsKey("techcontact"))
			replacements.put("techcontact", AppServerConstants.getTechContact());
		replacements.put("allparms", replacements.toString());
		
		int idx = 0;
		while (idx >= 0 && idx < sb.length()) {
			idx = sb.indexOf(AppServerConstants.REPLACEMENT_START, idx);
			if (idx < 0) {
				break;
			}
			int end = sb.indexOf(AppServerConstants.REPLACEMENT_END, idx + 2);
			if (end < 0) {
				break;
			}
			
			String key = sb.substring(idx + 2, end);
			if (replacements.containsKey(key)) {
//				int debug = idx < 10 ? 0 : idx - 10;
//				System.out.println("Replace " + key + " with " + replacements.get(key) + " at " + idx + " to " + (end + 2));
//				System.out.println(debug + ":" + sb.substring(debug, debug + 40));
				sb.replace(idx, end + 2, replacements.get(key));
				idx = idx + replacements.get(key).length();
//				System.out.println("Continue from " + idx);
//				System.out.println(debug + ":" + sb.substring(debug, debug + 40));
			} else {
				idx = end + 2;
			}
		}
		
//		System.out.println("*****************************************");
//		System.out.println(sb);
		
		return sb.toString();
	}
    
    private static void logConsole(String message) {
    	System.out.println(new Date() + " : " + CLASS_NAME + "~ " + message);
    }

}
