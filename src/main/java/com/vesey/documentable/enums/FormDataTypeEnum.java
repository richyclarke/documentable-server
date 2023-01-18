package com.vesey.documentable.enums;

public enum FormDataTypeEnum {
	STRING("STRING"), INTEGER("INTEGER"), DECIMAL("DECIMAL"), CURRENCY("CURRENCY"), DATE("DATE"), SELECT("SELECT"), BOOLEAN("BOOLEAN"),;

	private String value;

	FormDataTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static FormDataTypeEnum fromValue(String value) {
		for (FormDataTypeEnum thisValue : FormDataTypeEnum.values()) {
			if (thisValue.getValue().equals(value)) {
				return thisValue;
			}
		}
		throw new UnsupportedOperationException("The value " + value + " is not supported!");
	}
}
