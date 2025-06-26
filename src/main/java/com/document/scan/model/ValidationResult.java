package com.document.scan.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
	private String email;
	private boolean validFormat;
	private boolean domainExists;
	private boolean hasMxRecord;
	private String error;
	private List<String> mxRecords;

	// Constructors
	public ValidationResult(String email) {
		this.email = email;
		this.mxRecords = new ArrayList<>();
	}

	// Getters and setters
	public String getEmail() {
		return email;
	}

	public boolean isValidFormat() {
		return validFormat;
	}

	public void setValidFormat(boolean validFormat) {
		this.validFormat = validFormat;
	}

	public boolean isDomainExists() {
		return domainExists;
	}

	public void setDomainExists(boolean domainExists) {
		this.domainExists = domainExists;
	}

	public boolean isHasMxRecord() {
		return hasMxRecord;
	}

	public void setHasMxRecord(boolean hasMxRecord) {
		this.hasMxRecord = hasMxRecord;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<String> getMxRecords() {
		return mxRecords;
	}

	public void setMxRecords(List<String> mxRecords) {
		this.mxRecords = mxRecords;
	}

	public boolean isValid() {
		return validFormat && domainExists && hasMxRecord;
	}

	@Override
	public String toString() {
		return String.format("Email: %s, Valid: %s, Format: %s, Domain: %s, MX: %s, Error: %s", email, isValid(),
				validFormat, domainExists, hasMxRecord, error);
	}
}