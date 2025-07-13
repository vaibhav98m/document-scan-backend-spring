package com.document.scan.model;

import org.springframework.format.annotation.NumberFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Valid
public class OtpRequest {

	@Email(message = "Please provide a valid email address")
	@NotBlank(message = "Email is required")
	private String email;

}