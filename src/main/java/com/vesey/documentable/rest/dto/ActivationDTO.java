package com.vesey.documentable.rest.dto;

public class ActivationDTO {

	private String username;
	private String uuid;

	public ActivationDTO() {

	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}