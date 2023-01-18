package com.vesey.documentable.enums;

public enum DatasourceTypeEnum {
	MANUAL_ENTRY("MANUAL_ENTRY"), DOCUMENT("DOCUMENT"), SEARCHPOINT_CASE("SEARCHPOINT_CASE");

	private String value;

	DatasourceTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static DatasourceTypeEnum fromValue(String value) {
		for (DatasourceTypeEnum thisValue : DatasourceTypeEnum.values()) {
			if (thisValue.getValue().equals(value)) {
				return thisValue;
			}
		}
		throw new UnsupportedOperationException("The value " + value + " is not supported!");
	}
}
