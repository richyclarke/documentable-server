package com.vesey.documentable.enums;

public enum MatterTypeEnum {
	SEARCHPOINT_CASE("SEARCHPOINT_CASE"), GENERIC("GENERIC");

	private String value;

	MatterTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static MatterTypeEnum fromValue(String value) {
		for (MatterTypeEnum thisValue : MatterTypeEnum.values()) {
			if (thisValue.getValue().equals(value)) {
				return thisValue;
			}
		}
		throw new UnsupportedOperationException("The value " + value + " is not supported!");
	}
}
