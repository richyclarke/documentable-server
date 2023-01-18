package com.vesey.documentable.rest.dto;

import java.util.HashMap;
import java.util.Map;

public class CountsDTO {

	private Map<String, Integer> counts;

	public Map<String, Integer> getCounts() {
		if (counts == null) {
			counts = new HashMap<>();
		}
		return counts;
	}

	public void setCounts(Map<String, Integer> counts) {
		this.counts = counts;
	}

}
