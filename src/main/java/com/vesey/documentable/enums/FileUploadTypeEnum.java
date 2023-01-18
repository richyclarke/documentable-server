package com.vesey.documentable.enums;

public enum FileUploadTypeEnum {
	USER_PROFILE_PICTURE("USER_PROFILE_PICTURE"), RECEIPT("RECEIPT"), TRIP_LOGO("TRIP_LOGO");

	private String value;

	FileUploadTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static FileUploadTypeEnum fromValue(String value) {
		for (FileUploadTypeEnum thisValue : FileUploadTypeEnum.values()) {
			if (thisValue.getValue().equals(value)) {
				return thisValue;
			}
		}
		throw new UnsupportedOperationException("The value " + value + " is not supported!");
	}
}
