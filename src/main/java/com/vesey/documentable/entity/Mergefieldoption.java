package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.MergefieldoptionBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Mergefieldoption extends MergefieldoptionBase {

	public static final long serialVersionUID = 1L;

	public Mergefieldoption() {

	}

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Mergefieldoption user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Mergefieldoption getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Mergefieldoption.class, id);
	}

	@JsonIgnore
	@Transient
	public static Mergefieldoption getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Mergefieldoption.class, "select Object(x) from Mergefieldoption x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Collection<Mergefieldoption> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Mergefieldoption x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Mergefieldoption.class, sql, null, false);
	}

}
