package com.vesey.documentable.entity.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Document")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDTO extends BaseEntityDTO {

	private UserDTO createdby;
	private UserDTO lastmodifiedby;
	private String name;
	private String description;
	private Date createddate;
	private Date lastmodifieddate;
	private String matterUuid;
	private String documenttemplateUuid;
	private boolean numbered;
	private String documenttemplatename;

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

	public String getMatterUuid() {
		return matterUuid;
	}

	public void setMatterUuid(String matterUuid) {
		this.matterUuid = matterUuid;
	}

	public String getDocumenttemplateUuid() {
		return documenttemplateUuid;
	}

	public void setDocumenttemplateUuid(String documenttemplateUuid) {
		this.documenttemplateUuid = documenttemplateUuid;
	}

	public UserDTO getCreatedby() {
		return createdby;
	}

	public void setCreatedby(UserDTO createdby) {
		this.createdby = createdby;
	}

	public UserDTO getLastmodifiedby() {
		return lastmodifiedby;
	}

	public void setLastmodifiedby(UserDTO lastmodifiedby) {
		this.lastmodifiedby = lastmodifiedby;
	}

	public Date getLastmodifieddate() {
		return lastmodifieddate;
	}

	public void setLastmodifieddate(Date lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}

	public boolean isNumbered() {
		return numbered;
	}

	public void setNumbered(boolean numbered) {
		this.numbered = numbered;
	}

	public String getDocumenttemplatename() {
		return documenttemplatename;
	}

	public void setDocumenttemplatename(String documenttemplatename) {
		this.documenttemplatename = documenttemplatename;
	}

}
