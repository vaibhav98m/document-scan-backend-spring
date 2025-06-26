package com.document.scan.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentInsights {
    private String id;
    private String documentId;
    private List<KeyInsight> insights;
    private Date generatedAt;

    public DocumentInsights() {}

    public DocumentInsights(String id, String documentId, List<KeyInsight> insights, Date generatedAt) {
        this.id = id;
        this.documentId = documentId;
        this.insights = insights;
        this.generatedAt = generatedAt;
    }
}