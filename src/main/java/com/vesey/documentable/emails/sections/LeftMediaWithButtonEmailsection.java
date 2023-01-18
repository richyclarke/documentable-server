package com.vesey.documentable.emails.sections;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vesey.documentable.entity.Emailsection;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.session.DBFacade;

import freemarker.template.TemplateException;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class LeftMediaWithButtonEmailsection extends Emailsection {

	private static final long serialVersionUID = 1L;

	public Integer getSectionID() {
		return 105;
	}

	private String title;
	private String body;

	private String imageURL;
	private String imageLinkURL;

	private String linkText;
	private String linkURL;

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {

		// now populate this section
		Map<String, String> map = new HashMap<>();
		map.putAll(baseMap);

		map.put("title", title);
		map.put("body", body);

		map.put("imageURL", imageURL);
		map.put("imageLinkURL", imageLinkURL);

		map.put("linkText", linkText);
		map.put("linkURL", linkURL);

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

	public String getImageLinkURL() {
		return imageLinkURL;
	}

	public void setImageLinkURL(String imageLinkURL) {
		this.imageLinkURL = imageLinkURL;
	}

}
