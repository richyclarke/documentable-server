package com.vesey.documentable.entity.dto;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Rulegroup")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RulegroupDTO extends BaseEntityDTO {

	private String snippettemplateUuid; // the snippettemplate to add to when adding new rulegroups

	private Collection<RuleDTO> rules;

	public Collection<RuleDTO> getRules() {
		return rules;
	}

	public void setRules(Collection<RuleDTO> rules) {
		this.rules = rules;
	}

	public String getSnippettemplateUuid() {
		return snippettemplateUuid;
	}

	public void setSnippettemplateUuid(String snippettemplateUuid) {
		this.snippettemplateUuid = snippettemplateUuid;
	}
}
