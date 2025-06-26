package com.document.scan.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryResponse {
	private String id;
	private String documentId;
	private String question;
	private String answer;
	private Date timestamp;
	private Double confidence;

	public QueryResponse() {
	}

	public QueryResponse(String id, String documentId, String question, String answer, Date timestamp,
			Double confidence) {
		this.id = id;
		this.documentId = documentId;
		this.question = question;
		this.answer = answer;
		this.timestamp = timestamp;
		this.confidence = confidence;
	}
}