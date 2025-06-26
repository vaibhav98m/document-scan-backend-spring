package com.document.scan.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentSummary {
    private String id;
    private String documentId;
    private String summaryData;
    private Date generatedAt;
    private List<String> keyPoints;
    private Integer wordCount;
    private Integer readingTime;

    public DocumentSummary() {}

    public DocumentSummary(String id, String documentId, String summaryData, Date generatedAt, List<String> keyPoints) {
        this.id = id;
        this.documentId = documentId;
        this.summaryData = summaryData;
        this.generatedAt = generatedAt;
        this.keyPoints = keyPoints;
    }
}