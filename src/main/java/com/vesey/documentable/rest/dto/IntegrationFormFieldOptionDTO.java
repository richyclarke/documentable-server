package com.vesey.documentable.rest.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class IntegrationFormFieldOptionDTO {
	@NotNull
	@Schema(required = true, description = "The label that appears in the drop down when selecting options, e.g. 'Married' or 'Single'")
	private String label;
	@Schema(description = "The value returned when this option is selected, e.g. 'MARRIED', 'SINGLE'. If not provided, defaults to label.")
	private String value;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
