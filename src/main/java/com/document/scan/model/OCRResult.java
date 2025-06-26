package com.document.scan.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OCRResult {
	private String text;
	private List<WordConfidence> words;
	private double averageConfidence;

	public OCRResult(String text, List<WordConfidence> words) {
		this.text = text;
		this.words = words;
		this.averageConfidence = words.stream().mapToDouble(WordConfidence::getConfidence).average().orElse(0.0);
	}

}
