package com.document.scan.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryResponse {

	private Integer id;
	private Integer documentId;

	private String documentName;

	private String question;
	private String answer;
	private Date timestamp;
	private Double confidence;

	public QueryResponse() {
	}

	public QueryResponse(Integer id, Integer documentId, String documentName, String question, String answer,
			Date timestamp, Double confidence) {
		this.id = id;
		this.documentId = documentId;
		this.documentName = documentName;
		this.question = question;
		this.answer = answer;
		this.timestamp = timestamp;
		this.confidence = confidence;
	}
}