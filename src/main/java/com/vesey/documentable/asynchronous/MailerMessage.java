package com.vesey.documentable.asynchronous;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;

public class MailerMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mailContent;
	private String subjectLine;
	private Collection<String> toAddresses;
	private Collection<String> ccAddresses;
	private Collection<String> bccAddresses;
	private List<String> attachments;
	private String fromAddress;
	private Boolean readReceipt;
	private Integer componentId;
	private Boolean direct;

	private final Logger log = Logger.getLogger(MailerMessage.class);

	public Boolean sendRequest(ConnectionFactory connectionFactory) {
		log.info("sendRequest: Start");
		Connection connection = null;
		Session session = null;
		MessageProducer messageProducer = null;

		InitialContext initialContext = null;
		try {
			initialContext = new InitialContext();
		} catch (NamingException ex) {
			log.error("sendRequest: Exception occurred creating Initial Context: " + ex.getLocalizedMessage() + " - " + ex.getExplanation());
			return false;
		}
		// log.info("sendRequest: Created Initial Context");

		if (connectionFactory == null) {
			log.error("sendRequest: connectionFactory is null. could not send mail message");
			return false;
		}
		// log.info("sendRequest: Connection Factory Created");

		Queue queue = null;

		try {
			queue = (Queue) initialContext.lookup("queue/documentable_mailerQueue");
		} catch (NamingException ex) {
			log.error("sendRequest: Exception occurred looking up queue: " + ex.getLocalizedMessage() + " - " + ex.getExplanation());
			return false;
		}
		// log.info("sendRequest: Queue Created");

		try {
			connection = connectionFactory.createConnection();
		} catch (JMSException ex) {
			log.error("sendRequest: Exception occurred creating connection: " + ex.getLocalizedMessage());
			return false;
		}
		// log.info("sendRequest: Connection Created");

		try {
			session = connection.createSession();
		} catch (JMSException ex) {
			log.error("sendRequest: Exception occurred creating Session: " + ex.getLocalizedMessage());
			return false;
		}
		// log.info("sendRequest: Session Created");

		try {
			messageProducer = session.createProducer(queue);
		} catch (JMSException ex) {
			log.error("sendRequest: Exception occurred creating producer: " + ex.getLocalizedMessage());
			return false;
		}
		// log.info("sendRequest: MessageProducer Created");

		ObjectMessage oMessage = null;

		try {
			oMessage = session.createObjectMessage();
		} catch (JMSException ex) {
			log.error("sendRequest: Exception occurred creating object message: " + ex.getLocalizedMessage());
			return false;
		}
		// log.info("sendRequest: Message Created");

		try {
			oMessage.setObject(this);
		} catch (Exception ex) {
			log.error("sendRequest: Exception occurred setting object : " + ex.getLocalizedMessage());
			return false;
		}
		// log.info("sendRequest: Message Object Set");

		try {
			messageProducer.send(oMessage);
			log.info("sendRequest: Message Sent to JMS Mailer Queue.");
		} catch (JMSException ex) {
			log.error("sendRequest: Exception occurred sending message: " + ex.getLocalizedMessage());
			return false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					log.error("sendRequest: Exception closing connection : " + e);
				}
			}
		}
		log.info("sendRequest: End.");
		return true;
	}

	// getters and setters

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getSubjectLine() {
		return subjectLine;
	}

	public void setSubjectLine(String subjectLine) {
		this.subjectLine = subjectLine;
	}

	public Collection<String> getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(Collection<String> toAddresses) {
		this.toAddresses = toAddresses;
	}

	public Collection<String> getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(Collection<String> ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	public Collection<String> getBccAddresses() {
		return bccAddresses;
	}

	public void setBccAddresses(Collection<String> bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public Boolean getReadReceipt() {
		return readReceipt;
	}

	public void setReadReceipt(Boolean readReceipt) {
		this.readReceipt = readReceipt;
	}

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public Boolean getDirect() {
		return direct;
	}

	public void setDirect(Boolean direct) {
		this.direct = direct;
	}

}
