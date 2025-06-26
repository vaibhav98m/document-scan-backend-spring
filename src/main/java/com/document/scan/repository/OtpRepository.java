package com.document.scan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.document.scan.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long> {
	Optional<Otp> findTopByEmailOrderByExpiryTimeDesc(String email);
}
