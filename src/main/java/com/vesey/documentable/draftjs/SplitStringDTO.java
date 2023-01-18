package com.vesey.documentable.draftjs;

import java.util.ArrayList;
import java.util.List;

public class SplitStringDTO {

	private int start;
	private int end;
	private String text;
	private String replacementText;
	private List<String> style;

	public SplitStringDTO(int start, int end, String text) {
		this.start = start;
		this.end = end;
		this.text = text;
		style = new ArrayList<>();

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getStyle() {
		return style;
	}

	public void setStyle(List<String> style) {
		this.style = style;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getReplacementText() {
		return replacementText;
	}

	public void setReplacementText(String replacementText) {
		this.replacementText = replacementText;
	}
}
