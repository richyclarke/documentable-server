package com.vesey.documentable.entity;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.vesey.documentable.entity.base.EmailBase;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.freemarker.FreemarkerUser;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import freemarker.template.TemplateException;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity

public class Email extends EmailBase {

	public static final long serialVersionUID = 1L;

	public String populate(DBFacade dbFacade, Map<String, String> baseMap) throws TemplateException {
		throw new UnsupportedOperationException();
	}

	public Map<String, String> getSampleData(DBFacade dbFacade, Users user) throws ConflictException {
		throw new UnsupportedOperationException();
	}

	public List<String> getSectionLayout() {
		throw new UnsupportedOperationException();
	}

	@JsonIgnore
	@Transient
	public static Email getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Email.class, id);
	}

	@JsonIgnore
	@Transient
	public static List<Email> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Email x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Email.class, sql, null, false);
	}

	public Email clone() {

		try {
			Class<?> c = Class.forName(classname);
			Email inst = (Email) c.newInstance();
			inst.setContent(this.getContent());
			inst.setClassname(this.getClassname());
			inst.setId(this.getId());
			inst.setName(this.getName());
			inst.setDescription(this.getDescription());
			inst.setEmailtemplate(this.getEmailtemplate());
			inst.setSubject(this.getSubject());
			return inst;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// can't just merge because of subtypes
	public Boolean save(DBFacade dbFacade) throws ConflictException {
		if (this.id != null) {
			// merge
			Email existing = getById(dbFacade, id);
			existing = copyFields(existing);
			dbFacade.merge(existing);
		} else {
			Email inst = new Email();
			inst = copyFields(inst);

			dbFacade.persist(inst);
			this.id = inst.id;
		}
		return true;
	}

	public Email copyFields(Email email) {
		email.setClassname(this.classname);
		email.setContent(this.content);
		email.setDescription(this.description);
		email.setEmailtemplate(this.emailtemplate);
		email.setId(this.id);
		email.setName(this.name);
		email.setSubject(this.subject);

		return email;
	}

	public <T extends Object> T build(Map<String, String> variableMap) throws TemplateException {

		// substitute variableMap values into JSON data
		content = FreemarkerUser.mergeTemplate(variableMap, content);

		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ObjectReader objectReader = mapper.readerForUpdating(this);
		try {
			T updatedEmail = objectReader.readValue(content);
			return updatedEmail;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
