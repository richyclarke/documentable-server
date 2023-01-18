package com.vesey.documentable.asynchronous;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.logging.Logger;

import com.sun.mail.smtp.SMTPTransport;
import com.vesey.documentable.session.Emailer;
import com.vesey.documentable.utils.Utils;

@Named
@MessageDriven(name = "documentable_mailerQueue", mappedName = "java:jboss/jms/queue/documentable_mailerQueue", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/jms/queue/documentable_mailerQueue"),
		@ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1") })
public class MailerMDB implements MessageListener {

	private static Logger log = Logger.getLogger(MailerMDB.class.toString());

	private static int DELAY = 1000;

	@Inject
	transient Emailer emailer;

	public MailerMDB() {

	}

	@Override
	public void onMessage(javax.jms.Message inMessage) {
		log.info(
				"onMessage: Start ----------------------------------------------- Mailer Message Received -------------------------------------->.");

		// ignore re-deliveries
		try {
			// log.info("onMessage: Is Re-delivery = " + inMessage.getJMSRedelivered());
			if (inMessage.getJMSRedelivered() == true) {
				log.warn("onMessage: Ignoring messsage re-delivery");
				return;
			}
		} catch (JMSException ex) {
			log.error("onMessage: JMS Exception on reading redelivered flag: " + ex.getLocalizedMessage());
			return;
		}

		if (!(inMessage instanceof ObjectMessage)) {
			log.error("onMessage: Could not process incoming message - not an object message: "
					+ inMessage.getClass().toString());
			return;
		}

		ObjectMessage message = (ObjectMessage) inMessage;
		Object payload;

		try {
			payload = message.getObject();
		} catch (JMSException ex) {
			log.error("onMessage: JMS Exception : " + ex.getLocalizedMessage());
			return;
		}

		if (payload == null) {
			log.error("onMessage: Payload is NULL");
			return;
		}

		// log.info("onMessage: payload type = " + payload.getClass());
		if (!(payload instanceof MailerMessage)) {
			log.error("onMessage: Could not process incoming message - not an MailerMessage: "
					+ payload.getClass().toString());
			return;
		}

		MailerMessage theMessage = (MailerMessage) payload;

		handleMessage(theMessage);

		log.info("onMessage: End");
	}

	public void handleMessage(MailerMessage message) {
		log.info("handleMessage: Start - see Mail Log for more info.");

		Collection<String> toAddresses;
		Collection<String> ccAddresses;
		Collection<String> bccAddresses;
		String subjectLine;
		String mailContent;
		String fromAddress;
		List<String> attachments;

		toAddresses = message.getToAddresses();
		ccAddresses = message.getCcAddresses();
		bccAddresses = message.getBccAddresses();
		subjectLine = message.getSubjectLine();
		mailContent = message.getMailContent();
		fromAddress = message.getFromAddress();
		attachments = message.getAttachments();

		// log.info("handleMessage: hostName = " + hostName);
		// log.info("handleMessage: hostPort = " + hostPort);
		@SuppressWarnings("unused")
		MailerMDB.Authenticator authenticator = null;
		if (Utils.smtpAuthenticationRequired()) {
			authenticator = new MailerMDB().new Authenticator();
			// log.info("handleMessage: email = " +
			// authenticator.getPasswordAuthentication().getEmail());
			// log.info("handleMessage: password = " +
			// authenticator.getPasswordAuthentication().getPassword());
		}
		// Before we send any messages, we need to setup the mail system..
		// log.info("handleMessage: toAddresses = " + toAddresses);

		{
			// log.info("handleMessage: ZZZZZZZZZZZZZZ Sleeping for " + DELAY / 1000 + "
			// seconds ZZZZZZZZZZZZZZZZ.");
			try {
				Thread.sleep(DELAY);
				// log.info("handleMessage: ZZZZZZZZZZZZZZ Waking After " + DELAY / 1000 + "
				// seconds ZZZZZZZZZZZZZZZZ.");
			} catch (InterruptedException ex) {
				log.error("handleMessage: Interrupted Exception: ", ex);

			}
		}

		if ((toAddresses == null) || (fromAddress == null) || (subjectLine == null) || (mailContent == null)
				|| (toAddresses.isEmpty()) || (fromAddress.equals("")) || (subjectLine.equals(""))
				|| (mailContent.equals(""))) {
			log.error("handleMessage: null value found.");

			// Email not set up correctly.
		} else {
			// Define a session object; create an instance of the Properties class and add
			// components
			String hostPort = Utils.getStringProperty("smtpPort");

			Properties mailProps = new Properties();
			mailProps.put("mail.transport.protocol", "smtp");
			mailProps.put("mail.smtp.port", hostPort);
			mailProps.put("mail.smtp.starttls.enable", "true");
			mailProps.put("mail.smtp.auth", "true");

			Session session = Session.getDefaultInstance(mailProps);

			// The mail system is ready to go, so we can now produce and send the messages
			Message msg = new MimeMessage(session);
			try {
				if (!Utils.isEmpty(attachments)) {
					// log.info("handleMessage: Email has attachments");
					// create the Multipart and add its parts to it
					Multipart mp = new MimeMultipart();

					MimeBodyPart mbp1 = new MimeBodyPart();
					try {
						mbp1.setContent(mailContent, "text/html; charset=utf-8");
						mbp1.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
					} catch (MessagingException ex) {
						log.error(ex);

					}
					try {
						mp.addBodyPart(mbp1);
					} catch (MessagingException ex) {
						log.error(ex);

					}

					for (String thisAttachment : attachments) {
						if (!Utils.isEmpty(thisAttachment)) {
							// attach the file to the message
							// create the second message part
							MimeBodyPart mbp2 = new MimeBodyPart();
							FileDataSource fds = new FileDataSource(thisAttachment);
							try {
								mbp2.setDataHandler(new DataHandler(fds));
							} catch (MessagingException ex) {
								log.error(ex);

							}

							mbp2.setFileName(fds.getName());

							try {
								mp.addBodyPart(mbp2);
							} catch (MessagingException ex) {
								log.error(ex);

							}
						}
					}
					try {
						// add the Multipart to the message
						msg.setContent(mp);
					} catch (MessagingException ex) {
						log.error(ex);

					}
				} else {
					msg.setContent(mailContent, "text/html; charset=utf-8");
					msg.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
				}

				if (!Utils.isEmpty(message.getComponentId())) {
					msg.setHeader("documentable-Component-ID", message.getComponentId().toString());
				}

				if (message.getReadReceipt()) {
					msg.setHeader("Disposition-Notification-To", "richard.clarke@veseypartnership.com");
				}

				msg.setFrom(new InternetAddress(fromAddress));
				String logToString = "";

				Iterator<String> iterator = toAddresses.iterator();
				List<InternetAddress> sendToAddresses = new ArrayList<>();
				String oneAddress;
				while (iterator.hasNext()) {
					oneAddress = iterator.next();

					log.info("handleMessage: Sending email to    : " + oneAddress);
					if (Utils.serverInTestMode()) {
						// Server is in test mode, so replace To address with one from the properties
						// file
						// so that random people don't end up getting emailed.

						log.warn("handleMessage: CONVERTING EMAIL ADDRESS AS SERVER IS IN TEST MODE ");
						oneAddress = Utils.getStringProperty("testEmailAddress");

						logToString = logToString + oneAddress + ". ";
						sendToAddresses.add(new InternetAddress(oneAddress));
					} else {
						logToString = logToString + oneAddress + ". ";
						sendToAddresses.add(new InternetAddress(oneAddress));
					}
				}
				msg.setRecipients(Message.RecipientType.TO,
						sendToAddresses.toArray(new InternetAddress[sendToAddresses.size()]));

				String ccLogToString = "";
				if (ccAddresses == null) {
					ccAddresses = new ArrayList<String>();
				}
				iterator = ccAddresses.iterator();
				List<InternetAddress> sendCcAddresses = new ArrayList<InternetAddress>();
				while (iterator.hasNext()) {
					oneAddress = iterator.next();
					if (Utils.serverInTestMode()) {

						oneAddress = Utils.getStringProperty("testEmailAddress");

						ccLogToString = ccLogToString + oneAddress + ". ";
						sendCcAddresses.add(new InternetAddress(oneAddress));
					} else {

						ccLogToString = ccLogToString + oneAddress + ". ";
						sendCcAddresses.add(new InternetAddress(oneAddress));
					}
				}
				msg.setRecipients(Message.RecipientType.CC,
						sendCcAddresses.toArray(new InternetAddress[sendCcAddresses.size()]));

				String bccLogToString = "";
				if (bccAddresses == null) {
					bccAddresses = new ArrayList<String>();
				}
				iterator = bccAddresses.iterator();
				List<InternetAddress> sendBccAddresses = new ArrayList<InternetAddress>();
				while (iterator.hasNext()) {
					oneAddress = iterator.next();
					if (Utils.serverInTestMode()) {
						// Server is in test mode, so replace To address with one from the properties
						// file
						// so that random people don't end up getting emailed.

						oneAddress = Utils.getStringProperty("testEmailAddress");

						bccLogToString = bccLogToString + oneAddress + ". ";
						sendBccAddresses.add(new InternetAddress(oneAddress));
					} else {

						bccLogToString = bccLogToString + oneAddress + ". ";
						sendBccAddresses.add(new InternetAddress(oneAddress));
					}
				}
				msg.setRecipients(Message.RecipientType.BCC,
						sendBccAddresses.toArray(new InternetAddress[sendBccAddresses.size()]));

				msg.setSubject(subjectLine);
				msg.setSentDate(new java.util.Date());

				String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

				sendToTransport(msg, session, message.getDirect());

			} catch (SendFailedException ex) {
				log.error("handleMessage: (Exception): Message = " + ex.getLocalizedMessage(), ex);

				int length;
				if (ex.getInvalidAddresses() != null) {
					length = ex.getInvalidAddresses().length;
				} else {
					length = 0;
				}
				for (int c = 0; c < length; c++) {
					log.error("handleMessage: (Exception): Invalid Addresses = "
							+ ex.getInvalidAddresses()[c].toString());

				}

				if (ex.getValidUnsentAddresses() != null) {
					length = ex.getValidUnsentAddresses().length;
				} else {
					length = 0;
				}
				for (int c = 0; c < length; c++) {
					log.error("handleMessage: (Exception): Valid Unsent Addresses = "
							+ ex.getValidUnsentAddresses()[c].toString());

				}

				if (ex.getValidSentAddresses() != null) {
					length = ex.getValidSentAddresses().length;
				} else {
					length = 0;
				}
				for (int c = 0; c < length; c++) {
					log.error("handleMessage: (Exception): Valid Sent Addresses = "
							+ ex.getValidSentAddresses()[c].toString());

				}
			} catch (MessagingException e) {
				log.error("handleMessage: (MessagingException): " + e.toString() + " (" + e.getMessage() + ")", e);

			} catch (Exception e) {
				log.error("handleMessage: (Exception): " + e.toString() + " (" + e.getMessage() + ")", e);

			}

		}
	}

	private void sendToTransport(Message msg, Session session, Boolean direct) throws MessagingException {

		Integer attempts = 0;
		boolean success = false;

		while (!success) {
			try {
				Transport transport = session.getTransport();

				String username;
				String password;
				String hostName;

				if (direct) {
					username = "MailSender";
					password = "Gl0bal99";
					hostName = "mail.searchpoint.co.uk";
				} else {
					username = Utils.getStringProperty("auth-user");
					password = Utils.getStringProperty("auth-password");
					hostName = Utils.getStringProperty("smtpServer");
				}

				transport.connect(hostName, username, password);

				transport.sendMessage(msg, msg.getAllRecipients());

				String response = ((SMTPTransport) transport).getLastServerResponse();

				success = true;
			} catch (MessagingException e) {
				log.error("sendToTransport: Error Sending Email - Exception", e);

				success = false;
				attempts++;
				if (attempts > 2) {
					success = false;
					log.error("sendToTransport: Error Sending Email - GIVING UP");

					return;
				}
				Random rndGen = new Random();
				Float delay = rndGen.nextFloat() * 60000; // anything upto 1 minute
				Integer delayInt = delay.intValue();

				try {
					Thread.sleep(delayInt);
				} catch (InterruptedException ex) {
					log.error("sendToTransport: Interrupted Exception: ", ex);

				}
			}
		}

	}

	// member inner class to set up the mail authenticator.
	private class Authenticator extends javax.mail.Authenticator {

		private final PasswordAuthentication authentication;

		public Authenticator() {
			String username = Utils.getStringProperty("auth-user");
			String password = Utils.getStringProperty("auth-password");
			authentication = new PasswordAuthentication(username, password);
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}

}
