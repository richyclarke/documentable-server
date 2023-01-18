package com.vesey.documentable.emails;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vesey.documentable.emails.sections.DarkShortHeaderEmailsection;
import com.vesey.documentable.emails.sections.DividerEmailsection;
import com.vesey.documentable.emails.sections.FooterEmailsection;
import com.vesey.documentable.emails.sections.LeftMediaWithButtonEmailsection;
import com.vesey.documentable.emails.sections.PreFooterEmailsection;
import com.vesey.documentable.emails.sections.SimpleTitleAndBodyEmailsection;
import com.vesey.documentable.emails.sections.ThreeBulletsEmailsection;
import com.vesey.documentable.emails.sections.TriangleEmailsection;
import com.vesey.documentable.entity.Email;
import com.vesey.documentable.entity.Emailsection;
import com.vesey.documentable.entity.Users;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;

import freemarker.template.TemplateException;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResetEmail extends Email {

	private static final long serialVersionUID = 1L;

	private DarkShortHeaderEmailsection headerEmailsection = new DarkShortHeaderEmailsection();
	private ThreeBulletsEmailsection threeBulletsEmailsection = new ThreeBulletsEmailsection();
	private SimpleTitleAndBodyEmailsection simpleTitleAndBodyEmailsection = new SimpleTitleAndBodyEmailsection();
	private LeftMediaWithButtonEmailsection leftMediaWithButtonEmailsection = new LeftMediaWithButtonEmailsection();
	private TriangleEmailsection triangleEmailsection1 = new TriangleEmailsection();
	private TriangleEmailsection triangleEmailsection2 = new TriangleEmailsection();
	private SimpleTitleAndBodyEmailsection simpleTitleAndBodyEmailsection2 = new SimpleTitleAndBodyEmailsection();
	private PreFooterEmailsection preFooterEmailsection = new PreFooterEmailsection();
	private DividerEmailsection dividerEmailsection = new DividerEmailsection();
	private FooterEmailsection footerEmailsection = new FooterEmailsection();

	@Override
	public Map<String, String> getSampleData(DBFacade dbFacade, Users user) throws ConflictException {
		if (user == null) {
			// not logged in - just use me for now.
			user = Users.getById(dbFacade, 1);
		}
		user.setPasswordresetexpiry(new DateTime().plusHours(1).toDate());
		user.setPasswordresetuuid(UUID.randomUUID().toString());
		user = dbFacade.merge(user);
		String url = Utils.getPasswordResetURL(user.getPasswordresetuuid());

		Map<String, String> variableMap = new HashMap<String, String>();

		variableMap.put("clientname", user.getName());
		variableMap.put("email", user.getEmail());
		variableMap.put("url", url);

		return variableMap;
	}

	@Override
	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {
		StringBuilder sb = new StringBuilder();

		addSection(dbFacade, baseMap, sb, headerEmailsection);
		addSection(dbFacade, baseMap, sb, triangleEmailsection1);
		addSection(dbFacade, baseMap, sb, simpleTitleAndBodyEmailsection);

		addSection(dbFacade, baseMap, sb, leftMediaWithButtonEmailsection);
		addSection(dbFacade, baseMap, sb, threeBulletsEmailsection);
		addSection(dbFacade, baseMap, sb, triangleEmailsection2);
		addSection(dbFacade, baseMap, sb, simpleTitleAndBodyEmailsection2);
		addSection(dbFacade, baseMap, sb, preFooterEmailsection);
		addSection(dbFacade, baseMap, sb, dividerEmailsection);
		addSection(dbFacade, baseMap, sb, footerEmailsection);

		return sb.toString();
	}

	public void addSection(DBFacade dbFacade, Map<String, String> baseMap, StringBuilder sb, Emailsection section) throws TemplateException {
		Emailsection baseES = Emailsection.getById(dbFacade, section.getSectionID());
		section.setContent(baseES.getContent());
		sb.append(section.populate(dbFacade, baseMap));
	}

	public SimpleTitleAndBodyEmailsection getSimpleTitleAndBodyEmailsection() {
		return simpleTitleAndBodyEmailsection;
	}

	public void setSimpleTitleAndBodyEmailsection(SimpleTitleAndBodyEmailsection simpleTitleAndBodyEmailsection) {
		this.simpleTitleAndBodyEmailsection = simpleTitleAndBodyEmailsection;
	}

	public FooterEmailsection getFooterEmailsection() {
		return footerEmailsection;
	}

	public void setFooterEmailsection(FooterEmailsection footerEmailsection) {
		this.footerEmailsection = footerEmailsection;
	}

	public DividerEmailsection getDividerEmailsection() {
		return dividerEmailsection;
	}

	public void setDividerEmailsection(DividerEmailsection dividerEmailsection) {
		this.dividerEmailsection = dividerEmailsection;
	}

	public PreFooterEmailsection getPreFooterEmailsection() {
		return preFooterEmailsection;
	}

	public void setPreFooterEmailsection(PreFooterEmailsection preFooterEmailsection) {
		this.preFooterEmailsection = preFooterEmailsection;
	}

	public ThreeBulletsEmailsection getThreeBulletsEmailsection() {
		return threeBulletsEmailsection;
	}

	public void setThreeBulletsEmailsection(ThreeBulletsEmailsection threeBulletsEmailsection) {
		this.threeBulletsEmailsection = threeBulletsEmailsection;
	}

	public DarkShortHeaderEmailsection getHeaderEmailsection() {
		return headerEmailsection;
	}

	public void setHeaderEmailsection(DarkShortHeaderEmailsection headerEmailsection) {
		this.headerEmailsection = headerEmailsection;
	}

	public LeftMediaWithButtonEmailsection getLeftMediaWithButtonEmailsection() {
		return leftMediaWithButtonEmailsection;
	}

	public void setLeftMediaWithButtonEmailsection(LeftMediaWithButtonEmailsection leftMediaWithButtonEmailsection) {
		this.leftMediaWithButtonEmailsection = leftMediaWithButtonEmailsection;
	}

	public SimpleTitleAndBodyEmailsection getSimpleTitleAndBodyEmailsection2() {
		return simpleTitleAndBodyEmailsection2;
	}

	public void setSimpleTitleAndBodyEmailsection2(SimpleTitleAndBodyEmailsection simpleTitleAndBodyEmailsection2) {
		this.simpleTitleAndBodyEmailsection2 = simpleTitleAndBodyEmailsection2;
	}

	public TriangleEmailsection getTriangleEmailsection1() {
		return triangleEmailsection1;
	}

	public void setTriangleEmailsection1(TriangleEmailsection triangleEmailsection1) {
		this.triangleEmailsection1 = triangleEmailsection1;
	}

	public TriangleEmailsection getTriangleEmailsection2() {
		return triangleEmailsection2;
	}

	public void setTriangleEmailsection2(TriangleEmailsection triangleEmailsection2) {
		this.triangleEmailsection2 = triangleEmailsection2;
	}
}
