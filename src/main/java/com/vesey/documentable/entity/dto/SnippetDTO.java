package com.vesey.documentable.entity.dto;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Snippet")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnippetDTO extends BaseEntityDTO {

	private String name;
	private String description;
	private String content;
	private Integer sortorder;
	private String parentUuid;
	private String documentUuid;
	private String snippettemplateUuid;
	private boolean numbered;

	@ArraySchema(schema = @Schema(implementation = SnippetDTO.class))
	protected Collection<SnippetDTO> snippets;

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

	public Collection<SnippetDTO> getSnippets() {
		return snippets;
	}

	public void setSnippets(Collection<SnippetDTO> snippets) {
		this.snippets = snippets;
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

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public String getDocumentUuid() {
		return documentUuid;
	}

	public void setDocumentUuid(String documentUuid) {
		this.documentUuid = documentUuid;
	}

	public String getSnippettemplateUuid() {
		return snippettemplateUuid;
	}

	public void setSnippettemplateUuid(String snippettemplateUuid) {
		this.snippettemplateUuid = snippettemplateUuid;
	}

	public boolean isNumbered() {
		return numbered;
	}

	public void setNumbered(boolean numbered) {
		this.numbered = numbered;
	}

}
