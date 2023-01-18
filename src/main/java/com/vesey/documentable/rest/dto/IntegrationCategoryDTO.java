package com.vesey.documentable.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class IntegrationCategoryDTO {

	@Schema(required = true, description = "The name of the category on the form (different categories appear on different tabs of the form). If the category doesn't already exist, a new form field type will be automatically created on first use.")
	@NotNull
	private String name;

	@Schema(description = "Description for the category that will appear as a subheading on the relevant form tab. Required if category has not been previously created. ")
	private String description;

	@Schema(description = "Optional sort order for the category (controls order of tabs on form).")
	private Integer sortorder;

	@Schema(description = "List of form fields and their values.")
	private List<IntegrationFormFieldDTO> fields;

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

	public List<IntegrationFormFieldDTO> getFields() {
		return fields;
	}

	public void setFields(List<IntegrationFormFieldDTO> fields) {
		this.fields = fields;
	}

	public Integer getSortorder() {
		return sortorder;
	}

	public void setSortorder(Integer sortorder) {
		this.sortorder = sortorder;
	}

}
