package com.vesey.documentable.rest.dto;

public class RegistrationDTO {

	private String username;
	private String email;
	private String password;
	private String organisationId;
	private String organisationUuid;

	public RegistrationDTO() {

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrganisationUuid() {
		return organisationUuid;
	}

	public void setOrganisationUuid(String organisationUuid) {
		this.organisationUuid = organisationUuid;
	}

	public String getOrganisationId() {
		return organisationId;
	}

	public void setOrganisationId(String organisationId) {
		this.organisationId = organisationId;
	}

}