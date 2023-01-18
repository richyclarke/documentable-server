package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.RuleBase;
import com.vesey.documentable.session.DBFacade;
import com.vesey.documentable.utils.Utils;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity

public class Rule extends RuleBase {

	public static final long serialVersionUID = 1L;

	public Rule() {
		uuid = UUID.randomUUID().toString();
	}

	@JsonIgnore
	@Transient
	public static Rule getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Rule.class, id);
	}

	@JsonIgnore
	@Transient
	public static Rule getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Rule.class, "select Object(x) from Rule x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Collection<Rule> getAll(DBFacade dbFacade, String orderBy) {
		String sql = "select Object(x) from Rule x";
		if (Utils.isNotEmpty(orderBy)) {
			sql += " order by " + orderBy;
		}
		return dbFacade.doListQuery(Rule.class, sql, null, false);
	}

}
