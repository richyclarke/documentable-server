package com.vesey.documentable.entity;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vesey.documentable.entity.base.RulesetBase;
import com.vesey.documentable.session.DBFacade;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator = JSOGGenerator.class)
@Entity
public class Ruleset extends RulesetBase {

	public static final long serialVersionUID = 1L;

	public Ruleset() {
		uuid = UUID.randomUUID().toString();
		operand = false;
	}

	@OneToMany(mappedBy = "ruleset")
	@OrderBy(clause = "id ASC")
	private Collection<Rulegroup> rulegroups;

	@JsonIgnore
	public static Map<String, Object> getListParams(Map<String, Object> requestMap, Ruleset user) {
		return null;
	}

	@JsonIgnore
	@Transient
	public static Ruleset getById(DBFacade dbFacade, Integer id) {
		return dbFacade.getEntity(Ruleset.class, id);
	}

	public Collection<Rulegroup> getRulegroups() {
		return rulegroups;
	}

	public void setRulegroups(Collection<Rulegroup> rulegroups) {
		this.rulegroups = rulegroups;
	}

}
