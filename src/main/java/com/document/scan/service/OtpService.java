package com.document.scan.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.apache.poi.sl.draw.geom.GuideIf.Op;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.document.scan.entity.OtpEntity;
import com.document.scan.entity.UserEntity;
import com.document.scan.repository.OtpRepository;
import com.document.scan.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    public void generateOtp(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        OtpEntity otp = new OtpEntity();
        otp.setEmail(email);
        otp.setCode(code);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        // Send OTP to user's email
        emailService.sendOtpEmail(email, code);
    }

    public Integer verifyOtp(String email, String code) {
        Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email);
        boolean isValid = otpOpt
                .map(otp -> otp.getCode().equals(code) && otp.getExpiryTime().isAfter(LocalDateTime.now()))
                .orElse(false);
        Integer userId = 0;
        if (isValid) {
            // otpRepository.delete(otpOpt.get());
            System.out.println("OTP verified successfully for email: " + email);

            Optional<UserEntity> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                System.out.println("User found with User ID : " + user.get().getId());
                userId = user.get().getId();
            }

            // .map(user -> user.getId()).orElse(0)

        }
        return userId;
    }
}
