package com.vesey.documentable.emails.sections;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vesey.documentable.entity.Emailsection;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.session.DBFacade;

import freemarker.template.TemplateException;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class HeaderEmailsection extends Emailsection {

	private static final long serialVersionUID = 1L;

	public Integer getSectionID() {
		return 2;
	}

	private String title;
	private String body;

	private String logoURL;
	private String logoLinkURL;

	private String imageURL;

	private String linkText;
	private String linkURL;

	private String secondaryLinkText;
	private String secondaryLinkURL;

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {

		// now populate this section
		Map<String, String> map = new HashMap<>();
		map.putAll(baseMap);

		map.put("title", title);
		map.put("body", body);
		map.put("logoURL", logoURL);
		map.put("logoLinkURL", logoLinkURL);
		map.put("imageURL", imageURL);
		map.put("linkText", linkText);
		map.put("linkURL", linkURL);
		map.put("secondaryLinkText", secondaryLinkText);
		map.put("secondaryLinkURL", secondaryLinkURL);

		// now fetch content from DB and merge
		Emailsection es = Emailsection.getById(dbFacade, getSectionID());
		content = es.getContent();
		String filledContent = FreemarkerUser.mergeTemplate(map, es.getContent());

		return filledContent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLogoLinkURL() {
		return logoLinkURL;
	}

	public void setLogoLinkURL(String logoLinkURL) {
		this.logoLinkURL = logoLinkURL;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public String getSecondaryLinkText() {
		return secondaryLinkText;
	}

	public void setSecondaryLinkText(String secondaryLinkText) {
		this.secondaryLinkText = secondaryLinkText;
	}

	public String getSecondaryLinkURL() {
		return secondaryLinkURL;
	}

	public void setSecondaryLinkURL(String secondaryLinkURL) {
		this.secondaryLinkURL = secondaryLinkURL;
	}

}
