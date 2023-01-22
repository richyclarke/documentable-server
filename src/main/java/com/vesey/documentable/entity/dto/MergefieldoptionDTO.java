package com.vesey.documentable.entity.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Mergefieldoption")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MergefieldoptionDTO extends BaseEntityDTO {

	private String id; // so we can iterate over this DTO directly in react
	private String label;
	private String key; // for uuid to give a key to all iterations in javascript
	private Integer sortorder;
	private String mergefieldtemplateUuid;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMergefieldtemplateUuid() {
		return mergefieldtemplateUuid;
	}

	public void setMergefieldtemplateUuid(String mergefieldtemplateUuid) {
		this.mergefieldtemplateUuid = mergefieldtemplateUuid;
	}

	public Integer getSortorder() {
		return sortorder;
	}

	public void setSortorder(Integer sortorder) {
		this.sortorder = sortorder;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
