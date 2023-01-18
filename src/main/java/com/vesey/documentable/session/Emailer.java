/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vesey.documentable.session;

import java.io.File;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;

import org.apache.commons.validator.routines.EmailValidator;
import org.jboss.logging.Logger;

import com.vesey.documentable.Constants;
import com.vesey.documentable.asynchronous.MailerMessage;
import com.vesey.documentable.emails.PasswordResetEmail;
import com.vesey.documentable.emails.RegistrationEmail;
import com.vesey.documentable.entity.Email;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.utils.Utils;

import freemarker.core.InvalidReferenceException;
import freemarker.template.TemplateException;

/**
 *
 * @author Richard Clarke
 */
@Named
@Singleton
@Startup
public class Emailer extends FreemarkerUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1318925436533091545L;
	private transient static final Logger log = Logger.getLogger(Emailer.class);
	@Inject
	private DBFacade dbFacade;

	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	SimpleDateFormat sdf3 = new SimpleDateFormat("MMMM yyyy");
	NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);

	public static final int REGISTRATION_EMAIL = 1000;
	public static final int PASSWORD_RESET_EMAIL = 1001;

	public static final int PASSWORD_RESET = 200;

	@PostConstruct
	private void init() {
		log.info("init: Start");

		log.info("init: End");
	}

	public void sendPasswordResetEmail(Users user, String theUrl) {
		log.info("sendPasswordResetEmail: Start");

		// clone to get specific instance (e.g. CredasWelcomeEmail)
		Email baseEmail = Email.getById(dbFacade, PASSWORD_RESET_EMAIL);
		PasswordResetEmail passwordResetEmail = (PasswordResetEmail) baseEmail.clone();

		Map<String, String> variableMap = new HashMap<String, String>();

		variableMap.put("email", user.getEmail());
		variableMap.put("clientname", Utils.makeSafe(user.getName()));
		variableMap.put("url", theUrl);

		Map<String, String> styleMap = new HashMap<String, String>();
		styleMap.put("darkColor", "#d31d5c");
		styleMap.put("highlightColor", "#fcb651");

		String mailContent = null;

		try {
			// build to copy map to json and set content to specific instance fields (polymorphic) - returns new built instance
			PasswordResetEmail builtEmail = passwordResetEmail.build(variableMap);

			// populate with styling
			mailContent = builtEmail.populate(dbFacade, styleMap);
		} catch (InvalidReferenceException e) {
			log.error("sendPasswordResetEmail: Exception: ", e);
			return;
		} catch (TemplateException e) {
			log.error("sendPasswordResetEmail: TemplateException: ", e);
			return;
		}

		// insert into template
		Map<String, String> map = new HashMap<String, String>();
		map = new HashMap<String, String>();
		map.putAll(styleMap);
		map.put("content", mailContent);
		try {
			mailContent = FreemarkerUser.mergeTemplate(map, baseEmail.getEmailtemplate().getContent());
			// add final styling (for base template)
			mailContent = FreemarkerUser.mergeTemplate(styleMap, mailContent);
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			log.error("sendPasswordResetEmail: Exception merging template:", e);
		}
		// Prepare Recipients
		Collection<String> toAddresses = new ArrayList<>();
		Collection<String> ccAddresses = new ArrayList<>();
		Collection<String> bccAddresses = new ArrayList<>();

		bccAddresses.add(Constants.EMAIL_ADDRESS_VESEY_TECHNICAL_DIRECTOR);
		toAddresses.add(user.getEmail());

		String subjectLine = baseEmail.getSubject();

		sendEmail(mailContent, subjectLine, toAddresses, ccAddresses, bccAddresses, null, "noreply@mates-away.com", false, null, false);
		log.info("sendPasswordResetEmail: End");
	}

	public void sendRegistrationEmail(String email, String name, String uuid) {
		log.info("sendRegistrationEmail: Start");

		// clone to get specific instance (e.g. CredasWelcomeEmail)
		Email baseEmail = dbFacade.getEntity(Email.class, REGISTRATION_EMAIL);
		RegistrationEmail welcomeEmail = (RegistrationEmail) baseEmail.clone();

		// populate JSON with variables
		Map<String, String> variableMap = new HashMap<String, String>();

		variableMap.put("email", email);
		variableMap.put("clientname", name);
		variableMap.put("activateurl", Utils.generateActivateURL(uuid));

		Map<String, String> styleMap = new HashMap<String, String>();
		styleMap.put("darkColor", "#344b61");
		styleMap.put("highlightColor", "#1AB394");

		String mailContent = null;

		try {
			// build to copy map to json and set content to specific instance fields (polymorphic) - returns new built instance
			RegistrationEmail builtEmail = welcomeEmail.build(variableMap);

			// populate with styling
			mailContent = builtEmail.populate(dbFacade, styleMap);
		} catch (InvalidReferenceException e) {
			log.error("sendRegistrationEmail: Exception: ", e);
			return;
		} catch (TemplateException e) {
			log.error("sendRegistrationEmail: TemplateException: ", e);
			return;
		}

		// insert into template
		Map<String, String> map = new HashMap<String, String>();
		map = new HashMap<String, String>();
		map.putAll(styleMap);
		map.put("content", mailContent);
		try {
			mailContent = FreemarkerUser.mergeTemplate(map, baseEmail.getEmailtemplate().getContent());
			// add final styling (for base template)
			mailContent = FreemarkerUser.mergeTemplate(styleMap, mailContent);
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			log.error("sendRegistrationEmail: Exception merging template: ", e);
		}
		// Prepare Recipients
		Collection<String> toAddresses = new ArrayList<>();
		Collection<String> ccAddresses = new ArrayList<>();
		Collection<String> bccAddresses = new ArrayList<>();

		bccAddresses.add(Constants.EMAIL_ADDRESS_VESEY_TECHNICAL_DIRECTOR);
		toAddresses.add(email);

		String subjectLine = baseEmail.getSubject();

		sendEmail(mailContent, subjectLine, toAddresses, ccAddresses, bccAddresses, null, "noreply@mates-away.com", false, null, false);
		log.info("sendCredasRegistrationEmail: End");
	}

	public String populateTemplate(String mailContent, String subjectLine, String templateContent) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("subject", Utils.makeSafe(subjectLine));
		map.put("content", Utils.makeSafe(mailContent));

		String content = null;
		try {
			content = FreemarkerUser.mergeTemplate(map, templateContent);
			return content;
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			log.error("populateTemplate: Exception merging template: ", e);
			return null;
		}

	}

	public Collection<String> checkEmailAddresses(Collection<String> addresses) {
		if (!Utils.isEmpty(addresses)) {
			Collection<String> newAddresses = new ArrayList<>();
			EmailValidator validator = EmailValidator.getInstance();
			for (String thisAddress : addresses) {
				String[] emailAddresses = thisAddress.split(";|:|,| ");
				for (String thisEmailAddress : emailAddresses) {
					if (validator.isValid(thisEmailAddress)) {
						newAddresses.add(thisEmailAddress.trim());
					}
				}
			}
			return newAddresses;
		}
		return null;
	}

	public boolean checkAttachments(ArrayList<String> filenames, Long maxSize) {
		log.info("checkAttachments: Start");
		log.info("checkAttachments: maxSize = " + maxSize);
		log.info("checkAttachments: filenames = " + filenames);
		if (!Utils.isEmpty(filenames)) {
			try {
				Long overallSize = 0l;
				for (String thisFilename : filenames) {
					File thisFile = new File(thisFilename);
					long size = thisFile.length();
					log.info("checkAttachments: file = " + thisFilename + ", size = " + size);
					overallSize += size;
				}
				log.info("checkAttachments: Overall Size = " + overallSize);
				if (overallSize < maxSize) {
					log.info("checkAttachments: End, returning TRUE (OK)");
					return true;
				}
			} catch (Exception e) {
				log.error("checkAttachments: Exception:", e);
			}
		} else {
			// no attachments
			log.info("checkAttachments: No attchments found, returning TRUE (OK!)");
			return true;
		}
		log.info("checkAttachments: End, returning FALSE (Too Big!)");
		return false;
	}

	public void sendEmail(String mailContent, String subjectLine, Collection<String> toAddresses, Collection<String> ccAddresses, Collection<String> bccAddresses,
			List<String> attachments, String fromAddress, Boolean readReceipt, Integer componentId, Boolean direct) {

		toAddresses = checkEmailAddresses(toAddresses);
		ccAddresses = checkEmailAddresses(ccAddresses);
		bccAddresses = checkEmailAddresses(bccAddresses);

		if (fromAddress == null) {
			fromAddress = Constants.EMAIL_ADDRESS_FROM;
		}

		if (!Utils.isEmpty(toAddresses)) {

			MailerMessage msg = new MailerMessage();

			msg.setAttachments(attachments);
			msg.setBccAddresses(bccAddresses);
			msg.setCcAddresses(ccAddresses);
			msg.setMailContent(mailContent);
			msg.setSubjectLine(subjectLine);
			msg.setToAddresses(toAddresses);
			msg.setFromAddress(fromAddress);
			msg.setReadReceipt(readReceipt);
			msg.setComponentId(componentId);
			msg.setDirect(direct);

			Boolean success = msg.sendRequest(connectionFactory);
			if (!success) {
				log.error("sendEmail: Message NOT sent");
			} else {
				log.info("sendEmail: End. Message Added to Mailer Queue");
			}

		} else {
			log.warn("sendEmail: No 'TO' email addresses - email not sent");
		}
	}

}
