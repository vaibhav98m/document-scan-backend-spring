package com.document.scan.exception;

import org.springframework.http.HttpStatusCode;

public class CustomExcetion extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private HttpStatusCode statusCode;

	public CustomExcetion(String message, String errocode) {
		super(message);
		this.errorCode = errocode;
	}

	public CustomExcetion(String message, String errocode, HttpStatusCode statusCode) {
		super(message);
		this.errorCode = errocode;
		this.statusCode = statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

}
