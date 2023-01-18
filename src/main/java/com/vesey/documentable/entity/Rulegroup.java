package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.RulegroupBase;
import com.vesey.documentable.session.DBFacade;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Rulegroup extends RulegroupBase {

	public static final long serialVersionUID = 1L;

	public Rulegroup() {

	}

	@OneToMany(mappedBy = "rulegroup")
	@OrderBy(clause = "id DESC")
	private Collection<Rule> rules;

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Rulegroup user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Rulegroup getByUuid(DBFacade dbFacade, String uuid) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		return dbFacade.getEntityQuery(Rulegroup.class, "select Object(x) from Rulegroup x where uuid = :uuid", params, false);
	}

	@JsonIgnore
	@Transient
	public static Rulegroup getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Rulegroup.class, id);
	}

	public Collection<Rule> getRules() {
		return rules;
	}

	public void setRules(Collection<Rule> rules) {
		this.rules = rules;
	}

}
