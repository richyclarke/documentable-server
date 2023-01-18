package com.vesey.documentable.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class IntegrationMatterDTO {

	@Schema(required = true, description = "Username and password of a registered Documentable user.")
	@NotNull
	private CredentialsDTO credentials;

	@Schema(required = true, description = "A name for this new matter.")
	@NotNull
	private String name;

	@Schema(description = "An optional description for the matter.")
	private String description;

	@Schema(description = "Your client reference.")
	private String reference;

	@Schema(description = "Your internal ID for the matter.")
	private String sourceId;

	@Schema(description = "An optional URL we can poll to refresh this matter (must be unique to this matter and return an IntegrationMatterDTO).")
	private String refreshUrl;

	@Schema(description = "A list of categories containing form fields.")
	private List<IntegrationCategoryDTO> categories;

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getRefreshUrl() {
		return refreshUrl;
	}

	public void setRefreshUrl(String refreshUrl) {
		this.refreshUrl = refreshUrl;
	}

	public List<IntegrationCategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<IntegrationCategoryDTO> categories) {
		this.categories = categories;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public CredentialsDTO getCredentials() {
		return credentials;
	}

	public void setCredentials(CredentialsDTO credentials) {
		this.credentials = credentials;
	}

}
