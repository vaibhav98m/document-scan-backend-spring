package com.document.scan.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.document.scan.model.ApiResponse;
import com.document.scan.model.OtpRequest;
import com.document.scan.model.OtpVerifyRequest;
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
	@PostMapping(path = "/request-otp", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<String>> requestOtp(@RequestBody @Valid OtpRequest otpRequest) {
		otpService.generateOtp(otpRequest.getEmail());

		return ResponseEntity.ok(new ApiResponse<>("Success", "OTP sent to email", null, null));
	}

	@Operation(summary = "Verify received OTP")
	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyOtp(
			@RequestBody @Valid OtpVerifyRequest otpVerifyRequest) {
		Integer userId = otpService.verifyOtp(otpVerifyRequest.getEmail(), otpVerifyRequest.getOtp());
		if (userId != null && userId > 0) {
			Map<String, String> userDetails = new HashMap<>();
			System.out.println("User ID: " + userId);
			System.out.println("User ID: " + userDetails);
			userDetails.put("userId", String.valueOf(userId));
			userDetails.put("accessToken", "dummyAccessToken"); // Replace with actual token generation logic
			userDetails.put("refreshToken", "dummyRefreshToken"); // Replace with actual token generation logic
			System.out.println("User ID: " + userDetails);
			return ResponseEntity
					.ok(new ApiResponse<>("Success", "OTP verified successfully. User logged in.", null, userDetails));
		} else {
			return ResponseEntity.status(401).body(new ApiResponse<>("Failed", "Invalid or expired OTP", null, null));
		}
	}
}
