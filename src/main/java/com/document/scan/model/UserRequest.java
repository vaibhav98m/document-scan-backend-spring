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
public class UserRequest {

	@Email(message = "Please provide a valid email address")
	@NotBlank(message = "Email is required")
	private String email;

	@NotBlank(message = "firstname is required")
	private String firstname;
	@NotBlank(message = "lastname is required")
	private String lastname;

	@NumberFormat(pattern = "^(?:\\+91)?[6-9]\\d{9}$")
	private String phone;

}