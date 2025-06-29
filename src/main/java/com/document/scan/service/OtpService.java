package com.document.scan.service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.document.scan.entity.OtpEntity;
import com.document.scan.repository.OtpRepository;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

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

    public boolean verifyOtp(String email, String code) {
        Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email);
        return otpOpt.map(otp ->
            otp.getCode().equals(code) && otp.getExpiryTime().isAfter(LocalDateTime.now())
        ).orElse(false);
    }
}
