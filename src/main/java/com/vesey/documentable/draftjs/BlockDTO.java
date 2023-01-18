package com.vesey.documentable.draftjs;

import java.util.Collection;
import java.util.Map;

public class BlockDTO {
	private String key;
	private String text;
	private String type;
	private Integer depth;
	private Collection<InlineStyleRangeDTO> inlineStyleRanges;
	private Collection<EntityRangeDTO> entityRanges;
	private Map<String, Object> data;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Collection<InlineStyleRangeDTO> getInlineStyleRanges() {
		return inlineStyleRanges;
	}

	public void setInlineStyleRanges(Collection<InlineStyleRangeDTO> inlineStyleRanges) {
		this.inlineStyleRanges = inlineStyleRanges;
	}

	public Collection<EntityRangeDTO> getEntityRanges() {
		return entityRanges;
	}

	public void setEntityRanges(Collection<EntityRangeDTO> entityRanges) {
		this.entityRanges = entityRanges;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
