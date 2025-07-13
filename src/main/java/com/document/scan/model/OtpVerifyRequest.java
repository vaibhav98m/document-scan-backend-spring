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
public class OtpVerifyRequest {

	@Email(message = "Please provide a valid email address")
	@NotBlank(message = "Email is required")
	private String email;

	@NotBlank(message = "OTP is required")
	@NumberFormat(pattern = "^[0-9]{6}$") // Assuming OTP is a 6-digit number
	private String otp;

}