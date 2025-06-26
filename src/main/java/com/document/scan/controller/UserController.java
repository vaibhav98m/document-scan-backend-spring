package com.document.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.document.scan.entity.User;
import com.document.scan.model.ApiResponse;
import com.document.scan.model.UserRequest;
import com.document.scan.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Onboarding", description = "Handles user registration")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Onboard a new user")
    @PostMapping("/onboard")
    public ResponseEntity<ApiResponse<User>> onboardUser(@Valid @RequestBody UserRequest request) {
        User user = userService.onboardUser(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "User onboarded successfully", null, user));
    }
}
