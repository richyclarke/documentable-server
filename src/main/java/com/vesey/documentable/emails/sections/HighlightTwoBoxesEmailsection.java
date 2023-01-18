package com.vesey.documentable.emails.sections;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vesey.documentable.entity.Emailsection;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.session.DBFacade;

import freemarker.template.TemplateException;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class HighlightTwoBoxesEmailsection extends Emailsection {

	private static final long serialVersionUID = 1L;

	public Integer getSectionID() {
		return 101;
	}

	private String title;
	private String body;
	private AvatarPanelEmailsection panel1;
	private AvatarPanelEmailsection panel2;

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {

		// populate subsections
		Map<String, String> map = new HashMap<>();
		map.putAll(baseMap);
		if (panel1 != null) {
			panel1.setAlign("left");
			Emailsection baseES = Emailsection.getById(dbFacade, panel1.getSectionID());
			panel1.setContent(baseES.getContent());
			map.put("panel1", panel1.populate(dbFacade, baseMap));
		}
		if (panel2 != null) {
			panel2.setAlign("right");
			Emailsection baseES = Emailsection.getById(dbFacade, panel2.getSectionID());
			panel2.setContent(baseES.getContent());
			map.put("panel2", panel2.populate(dbFacade, baseMap));
		}

		// now populate this section
		map.put("title", title);
		map.put("body", body);

		// fetch content from DB and merge
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

	public AvatarPanelEmailsection getPanel1() {
		return panel1;
	}

	public void setPanel1(AvatarPanelEmailsection panel1) {
		this.panel1 = panel1;
	}

	public AvatarPanelEmailsection getPanel2() {
		return panel2;
	}

	public void setPanel2(AvatarPanelEmailsection panel2) {
		this.panel2 = panel2;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
