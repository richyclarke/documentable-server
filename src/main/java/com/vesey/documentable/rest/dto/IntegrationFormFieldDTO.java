package com.vesey.documentable.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.vesey.documentable.enums.FormDataTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;

public class IntegrationFormFieldDTO {

	@Schema(required = true, description = "A unique identifier which will be used under the covers as a placeholder for this form field. If the key is doesn't already exist, a new form field type will be automatically created with provided type, label, description and options.")
	@NotNull
	private String key;
	@Schema(required = true, description = "The populated value of this field. All values will be converted to the relevant type specified in the type field. INTEGER and DECIMAL types must be parseable as such, DATE types must be in the format 'dd/MM/YYYY hh:mm', BOOLEAN fields must be either 'true' or 'false' and 'SELECT' inputs must be equals to the 'value' field of their option.")
	@NotNull
	private String value;

	@Schema(description = "The enumerated type of this form field. Required if key has not been previously created.")
	private FormDataTypeEnum type;
	@Schema(description = "The label for the form field that will appears on the form.  Required if key has not been previously created.")
	private String label;
	@Schema(description = "A description which will appear as helper text under the form field on the form.  Required if key has not been previously created.")
	private String description;

	@Schema(description = "List of possible options. Only required for merge fields of type 'SELECT' and if key has not been previously created")
	private List<IntegrationFormFieldOptionDTO> options;

	public FormDataTypeEnum getType() {
		return type;
	}

	public void setType(FormDataTypeEnum type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<IntegrationFormFieldOptionDTO> getOptions() {
		return options;
	}

	public void setOptions(List<IntegrationFormFieldOptionDTO> options) {
		this.options = options;
	}
}
