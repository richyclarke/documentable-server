package com.vesey.documentable.entity.dto;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Snippettemplate")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnippettemplateDTO extends BaseEntityDTO {

	private String name;
	private String description;
	private String content;
	private Integer sortorder;
	private String parentUuid;
	private String documenttemplateUuid;
	private boolean numbered;

	private RulesetDTO ruleset;

	@ArraySchema(schema = @Schema(implementation = SnippettemplateDTO.class))
	protected Collection<SnippettemplateDTO> snippettemplates;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getSortorder() {
		return sortorder;
	}

	public void setSortorder(Integer sortorder) {
		this.sortorder = sortorder;
	}

	public Collection<SnippettemplateDTO> getSnippettemplates() {
		return snippettemplates;
	}

	public void setSnippettemplates(Collection<SnippettemplateDTO> snippettemplates) {
		this.snippettemplates = snippettemplates;
	}

	public RulesetDTO getRuleset() {
		return ruleset;
	}

	public void setRuleset(RulesetDTO ruleset) {
		this.ruleset = ruleset;
	}

	public String getDocumenttemplateUuid() {
		return documenttemplateUuid;
	}

	public void setDocumenttemplateUuid(String documenttemplateUuid) {
		this.documenttemplateUuid = documenttemplateUuid;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public boolean isNumbered() {
		return numbered;
	}

	public void setNumbered(boolean numbered) {
		this.numbered = numbered;
	}

}
