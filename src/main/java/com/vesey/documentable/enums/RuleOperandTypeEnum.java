package com.vesey.documentable.enums;

public enum RuleOperandTypeEnum {
	EQUALS("EQUALS"), NOT_EQUALS("NOT_EQUALS"), GREATER_THAN("GREATER_THAN"), LESS_THAN("LESS_THAN"), INCLUDES("INCLUDES"), STARTS_WITH("STARTS_WITH"), EXCLUDES("EXCLUDES");

	private String value;

	RuleOperandTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static RuleOperandTypeEnum fromValue(String value) {
		for (RuleOperandTypeEnum thisValue : RuleOperandTypeEnum.values()) {
			if (thisValue.getValue().equals(value)) {
				return thisValue;
			}
		}
		throw new UnsupportedOperationException("The value " + value + " is not supported!");
	}
}
