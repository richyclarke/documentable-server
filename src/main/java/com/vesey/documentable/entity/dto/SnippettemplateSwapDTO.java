package com.vesey.documentable.entity.dto;

public class SnippettemplateSwapDTO {

	private String sourceUuid;
	private String destinationUuid;
	private Integer index;

	public String getSourceUuid() {
		return sourceUuid;
	}

	public void setSourceUuid(String sourceUuid) {
		this.sourceUuid = sourceUuid;
	}

	public String getDestinationUuid() {
		return destinationUuid;
	}

	public void setDestinationUuid(String destinationUuid) {
		this.destinationUuid = destinationUuid;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}
