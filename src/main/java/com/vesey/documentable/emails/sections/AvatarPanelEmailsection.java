package com.vesey.documentable.emails.sections;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vesey.documentable.entity.Emailsection;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.session.DBFacade;

import freemarker.template.TemplateException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvatarPanelEmailsection extends Emailsection {

	private static final long serialVersionUID = 1L;

	public Integer getSectionID() {
		return 202;
	}

	private String align;
	private String imageURL;
	private String body;
	private String name;
	private String linkText;
	private String linkURL;

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> styleMap) throws TemplateException {

		// now populate this section
		Map<String, String> map = new HashMap<>();
		map.putAll(styleMap);
		map.put("imageURL", imageURL);
		map.put("body", body);
		map.put("name", name);
		map.put("linkText", linkText);
		map.put("linkURL", linkURL);
		map.put("align", align);

		// now fetch content from DB and merge
		Emailsection es = Emailsection.getById(dbFacade, getSectionID());
		content = es.getContent();
		String filledContent = null;

		filledContent = FreemarkerUser.mergeTemplate(map, es.getContent());

		return filledContent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}
}
