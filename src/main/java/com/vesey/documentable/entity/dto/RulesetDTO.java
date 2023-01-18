package com.vesey.documentable.entity.dto;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Ruleset")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RulesetDTO extends BaseEntityDTO {

	private boolean operand;
	private Collection<RulegroupDTO> rulegroups;
	private String snippettemplateUuid; // the snippettemplate to add to when adding new rulesets

	public boolean isOperand() {
		return operand;
	}

	public void setOperand(boolean operand) {
		this.operand = operand;
	}

	public Collection<RulegroupDTO> getRulegroups() {
		return rulegroups;
	}

	public void setRulegroups(Collection<RulegroupDTO> rulegroups) {
		this.rulegroups = rulegroups;
	}

	public String getSnippettemplateUuid() {
		return snippettemplateUuid;
	}

	public void setSnippettemplateUuid(String snippettemplateUuid) {
		this.snippettemplateUuid = snippettemplateUuid;
	}

}
