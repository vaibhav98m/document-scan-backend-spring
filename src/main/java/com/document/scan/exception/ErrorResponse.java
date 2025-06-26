package com.document.scan.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
public class ErrorResponse {

	@JsonProperty("faults")
	private List<Fault> faults = new ArrayList<>();

	public void addNewFault(Fault fault) {
		this.faults.add(fault);
	}
}
