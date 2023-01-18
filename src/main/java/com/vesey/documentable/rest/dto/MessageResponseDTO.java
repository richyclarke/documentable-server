package com.vesey.documentable.rest.dto;

public class MessageResponseDTO {

	private Boolean success;
	private String errorMessage;

	public MessageResponseDTO() {
		success = false;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

}
