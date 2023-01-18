package com.vesey.documentable.draftjs;

import java.util.Collection;
import java.util.Map;

public class ContentDTO {

	private Collection<BlockDTO> blocks;
	private Map<String, EntityDTO> entityMap;

	public Collection<BlockDTO> getBlocks() {
		return blocks;
	}

	public void setBlocks(Collection<BlockDTO> blocks) {
		this.blocks = blocks;
	}

	public Map<String, EntityDTO> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, EntityDTO> entityMap) {
		this.entityMap = entityMap;
	}

}
