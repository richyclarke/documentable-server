package com.vesey.documentable.emails.sections;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vesey.documentable.entity.Emailsection;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.session.DBFacade;

import freemarker.template.TemplateException;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class FooterEmailsection extends Emailsection {

	private static final long serialVersionUID = 1L;

	public Integer getSectionID() {
		return 3;
	}

	private String imageURL;
	private String linkText;
	private String linkURL;
	private FooterSocialPanelEmailsection socialpanel1 = new FooterSocialPanelEmailsection();
	private FooterSocialPanelEmailsection socialpanel2 = new FooterSocialPanelEmailsection();
	private FooterSocialPanelEmailsection socialpanel3 = new FooterSocialPanelEmailsection();

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {

		// now populate this section
		Map<String, String> map = new HashMap<>();
		map.putAll(baseMap);

		map.put("imageURL", imageURL);
		map.put("linkText", linkText);
		map.put("linkURL", linkURL);

		Emailsection baseES = Emailsection.getById(dbFacade, socialpanel1.getSectionID());
		socialpanel1.setContent(baseES.getContent());
		map.put("socialpanel1", socialpanel1.populate(dbFacade, baseMap));

		baseES = Emailsection.getById(dbFacade, socialpanel2.getSectionID());
		socialpanel2.setContent(baseES.getContent());
		map.put("socialpanel2", socialpanel2.populate(dbFacade, baseMap));

		baseES = Emailsection.getById(dbFacade, socialpanel3.getSectionID());
		socialpanel3.setContent(baseES.getContent());
		map.put("socialpanel3", socialpanel3.populate(dbFacade, baseMap));

		// now fetch content from DB and merge
		Emailsection es = Emailsection.getById(dbFacade, getSectionID());
		content = es.getContent();
		String filledContent = FreemarkerUser.mergeTemplate(map, es.getContent());

		return filledContent;
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

	public FooterSocialPanelEmailsection getSocialpanel1() {
		return socialpanel1;
	}

	public void setSocialpanel1(FooterSocialPanelEmailsection socialpanel1) {
		this.socialpanel1 = socialpanel1;
	}

	public FooterSocialPanelEmailsection getSocialpanel2() {
		return socialpanel2;
	}

	public void setSocialpanel2(FooterSocialPanelEmailsection socialpanel2) {
		this.socialpanel2 = socialpanel2;
	}

	public FooterSocialPanelEmailsection getSocialpanel3() {
		return socialpanel3;
	}

	public void setSocialpanel3(FooterSocialPanelEmailsection socialpanel3) {
		this.socialpanel3 = socialpanel3;
	}

}
