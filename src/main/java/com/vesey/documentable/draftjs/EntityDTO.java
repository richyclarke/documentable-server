package com.vesey.documentable.draftjs;

import java.util.Map;

public class EntityDTO {
	private String type;
	private String mutability;
	private Map<String, Object> data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getMutability() {
		return mutability;
	}

	public void setMutability(String mutability) {
		this.mutability = mutability;
	}

}
