package com.document.scan.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordConfidence {
	private String word;
	private double confidence;

	public WordConfidence(String word, double confidence) {
		this.word = word;
		this.confidence = confidence;
	}

}