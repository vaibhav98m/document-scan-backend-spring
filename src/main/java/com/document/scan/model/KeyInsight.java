package com.document.scan.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyInsight {
	private String id;
	private String type;
	private String priority;
	private String content;
	private double relevance;
	private String context;
	private String category;
	private List<String> tags;

	public KeyInsight() {
	}

	public KeyInsight(String id, String type, String priority, String content, double relevance, String context,
			String category, List<String> tags) {
		this.id = id;
		this.type = type;
		this.priority = priority;
		this.content = content;
		this.relevance = relevance;
		this.context = context;
		this.category = category;
		this.tags = tags;
	}

}
