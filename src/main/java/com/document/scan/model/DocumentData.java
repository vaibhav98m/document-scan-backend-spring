package com.document.scan.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentData {
	private String id;
	private String fileName;
	private long fileSize;
	private String fileType;
	private Date uploadedAt;
	private String previewUrl;
	private String content;

	public DocumentData() {
	}

	public DocumentData(String id, String fileName, long fileSize, String fileType, Date uploadedAt, String previewUrl,
			String content) {
		this.id = id;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileType = fileType;
		this.uploadedAt = uploadedAt;
		this.previewUrl = previewUrl;
		this.content = content;
	}
}