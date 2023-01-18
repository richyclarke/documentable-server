package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.OrganisationBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Organisation extends OrganisationBase {

	public static final long serialVersionUID = 1L;

	public Organisation() {

	}

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Organisation user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Organisation getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Organisation.class, id);
	}

	@JsonIgnore
	@Transient
	public static Organisation getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Organisation.class, "select Object(x) from Organisation x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Collection<Organisation> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Organisation x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Organisation.class, sql, null, false);
	}

}
