package com.vesey.documentable.enums;

public enum RoleTypeEnum {
	ADMIN("ADMIN"), CLIENT("CLIENT");

	private String value;

	RoleTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static RoleTypeEnum fromValue(String value) {
		for (RoleTypeEnum thisValue : RoleTypeEnum.values()) {
			if (thisValue.getValue().equals(value)) {
				return thisValue;
			}
		}
		throw new UnsupportedOperationException("The value " + value + " is not supported!");
	}
}
