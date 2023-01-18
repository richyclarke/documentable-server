package com.vesey.documentable.entity.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vesey.documentable.enums.MatterTypeEnum;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Matter")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatterDTO extends BaseEntityDTO {

	private String name;
	private String reference;
	private String description;
	private MatterTypeEnum type;
	private UserDTO createdby;
	private Date createddate;

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

	public Date getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public UserDTO getCreatedby() {
		return createdby;
	}

	public void setCreatedby(UserDTO createdby) {
		this.createdby = createdby;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public MatterTypeEnum getType() {
		return type;
	}

	public void setType(MatterTypeEnum type) {
		this.type = type;
	}

}
