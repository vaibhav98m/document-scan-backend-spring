package com.document.scan.model;

import javax.validation.constraints.NotNull;

import com.document.scan.annotation.ContentValidation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = false)
@Valid
public class QueryRequest {

	@Size(max = 1800, message = "Question must be less than 1800 characters")
	@NotBlank(message = "Question is required")
	@ContentValidation
	private String question;
	@NotNull(message = "User ID is required")
	private Integer userId;
	@NotNull(message = "Document ID is required")
	private Integer documentId;
}