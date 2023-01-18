package com.vesey.documentable.emails.sections;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vesey.documentable.entity.Emailsection;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.session.DBFacade;

import freemarker.template.TemplateException;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ThreeBulletsEmailsection extends Emailsection {

	private static final long serialVersionUID = 1L;

	public Integer getSectionID() {
		return 104;
	}

	private ThreeBulletsPanelEmailsection panel1;
	private ThreeBulletsPanelEmailsection panel2;
	private ThreeBulletsPanelEmailsection panel3;

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {

		// populate subsections
		Map<String, String> map = new HashMap<>();
		map.putAll(baseMap);
		if (panel1 != null) {
			Emailsection baseES = Emailsection.getById(dbFacade, panel1.getSectionID());
			panel1.setContent(baseES.getContent());
			map.put("panel1", panel1.populate(dbFacade, baseMap));
		}
		if (panel2 != null) {
			Emailsection baseES = Emailsection.getById(dbFacade, panel2.getSectionID());
			panel2.setContent(baseES.getContent());
			map.put("panel2", panel2.populate(dbFacade, baseMap));
		}
		if (panel2 != null) {
			Emailsection baseES = Emailsection.getById(dbFacade, panel3.getSectionID());
			panel3.setContent(baseES.getContent());
			map.put("panel3", panel3.populate(dbFacade, baseMap));
		}

		// fetch content from DB and merge
		Emailsection es = Emailsection.getById(dbFacade, getSectionID());
		content = es.getContent();
		String filledContent = FreemarkerUser.mergeTemplate(map, es.getContent());
		return filledContent;
	}

	public ThreeBulletsPanelEmailsection getPanel1() {
		return panel1;
	}

	public void setPanel1(ThreeBulletsPanelEmailsection panel1) {
		this.panel1 = panel1;
	}

	public ThreeBulletsPanelEmailsection getPanel2() {
		return panel2;
	}

	public void setPanel2(ThreeBulletsPanelEmailsection panel2) {
		this.panel2 = panel2;
	}

	public ThreeBulletsPanelEmailsection getPanel3() {
		return panel3;
	}

	public void setPanel3(ThreeBulletsPanelEmailsection panel3) {
		this.panel3 = panel3;
	}

}
