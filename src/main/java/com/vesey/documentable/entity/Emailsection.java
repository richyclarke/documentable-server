package com.vesey.documentable.entity;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vesey.documentable.entity.base.EmailsectionBase;
import com.vesey.documentable.errorhandling.ConflictException;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import freemarker.template.TemplateException;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Emailsection extends EmailsectionBase {

	public static final long serialVersionUID = 1L;

	public static Emailsection getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Emailsection.class, id);
	}

	public Integer getSectionID() {
		throw new UnsupportedOperationException();
	}

	public String populate(DBFacade dbFacade, Map<String, String> styleMap) throws TemplateException {
		throw new UnsupportedOperationException();
	}

	@JsonIgnore
	@Transient
	public static List<Emailsection> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Emailsection x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Emailsection.class, sql, null, false);
	}

	public Emailsection clone() {

		try {
			Class<?> c = Class.forName(classname);
			Emailsection inst = (Emailsection) c.getDeclaredConstructor().newInstance();
			inst.setContent(this.getContent());
			inst.setClassname(this.getClassname());
			inst.setId(this.getId());
			inst.setName(this.getName());
			inst.setVersion(this.getVersion());
			inst.setFields(this.getFields());
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
			Emailsection existing = getById(dbFacade, id);
			existing = copyFields(existing);
			dbFacade.merge(existing);
		} else {
			Emailsection inst = new Emailsection();
			inst = copyFields(inst);

			dbFacade.persist(inst);
			this.id = inst.id;
		}
		return true;
	}

	public Emailsection copyFields(Emailsection emailsection) {
		emailsection.setClassname(this.classname);
		emailsection.setContent(this.content);
		emailsection.setId(this.id);
		emailsection.setName(this.name);
		emailsection.setFields(this.fields);
		emailsection.setVersion(this.version);

		return emailsection;
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T build() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (T) mapper.readValue(content, this.getClass());
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
