package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.EmailtemplateBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity

public class Emailtemplate extends EmailtemplateBase {

	public static final long serialVersionUID = 1L;

	public Emailtemplate() {

	}

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Emailtemplate user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Emailtemplate getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Emailtemplate.class, id);
	}

	@JsonIgnore
	@Transient
	public static Collection<Emailtemplate> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Emailtemplate x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Emailtemplate.class, sql, null, false);
	}

}
