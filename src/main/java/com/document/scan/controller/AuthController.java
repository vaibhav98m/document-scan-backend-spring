package com.document.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.document.scan.model.KeyInsight;
import com.document.scan.service.OtpService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Handles OTP via Email login")
public class AuthController {

	@Autowired
	private OtpService otpService;

	@Operation(summary = "Send OTP to email")
	@PostMapping("/request-otp")
	public ResponseEntity<String> requestOtp(@RequestParam("email") String email) {
		otpService.generateOtp(email);
		return ResponseEntity.ok("OTP sent to your email");
	}

	@Operation(summary = "Verify received OTP")
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp) {
		boolean valid = otpService.verifyOtp(email, otp);
		if (valid) {
			// TODO: generate and return JWT
			return ResponseEntity.ok("OTP verified successfully. User logged in.");
		} else {
			return ResponseEntity.status(401).body("Invalid or expired OTP");
		}
	}
}
