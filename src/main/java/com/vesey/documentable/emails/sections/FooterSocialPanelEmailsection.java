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
public class FooterSocialPanelEmailsection extends Emailsection {

	private static final long serialVersionUID = 1L;

	public Integer getSectionID() {
		return 205;
	}

	private String linkImageURL;
	private String linkURL;

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {

		// now populate this section
		Map<String, String> map = new HashMap<>();
		map.putAll(baseMap);

		map.put("linkImageURL", linkImageURL);
		map.put("linkURL", linkURL);

		// now fetch content from DB and merge
		Emailsection es = Emailsection.getById(dbFacade, getSectionID());
		content = es.getContent();
		String filledContent = FreemarkerUser.mergeTemplate(map, es.getContent());
		return filledContent;
	}

	public String getLinkImageURL() {
		return linkImageURL;
	}

	public void setLinkImageURL(String linkImageURL) {
		this.linkImageURL = linkImageURL;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
}
