package com.vesey.documentable.rest.dto;

import com.vesey.documentable.entity.dto.UserDTO;

public class LoginResponseDTO {

	private UserDTO user;
	private String accessToken;

	private String error;

	public LoginResponseDTO(UserDTO user, String accessToken) {
		this.user = user;
		this.accessToken = accessToken;

		this.error = null;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
