package com.vesey.documentable.enums;

public enum FilerefTypeEnum {
	IMAGE("IMAGE"), RECEIPT("RECEIPT"), BOARDING_CARD("BOARDING_CARD"), ITINERARY("ITINERARY"), ROUTE_DETAILS("ROUTE_DETAILS"), ACCOMMODATION_DETAILS(
			"ACCOMMODATION_DETAILS"), CONTACT_DETAILS("CONTACT_DETAILS"), OTHER("OTHER"), MENU("MENU"), TICKET("TICKET");

	private String value;

	FilerefTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static FilerefTypeEnum fromValue(String value) {
		for (FilerefTypeEnum thisValue : FilerefTypeEnum.values()) {
			if (thisValue.getValue().equals(value)) {
				return thisValue;
			}
		}
		throw new UnsupportedOperationException("The value " + value + " is not supported!");
	}
}
