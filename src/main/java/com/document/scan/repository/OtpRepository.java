package com.document.scan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.document.scan.entity.OtpEntity;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
	Optional<OtpEntity> findTopByEmailOrderByExpiryTimeDesc(String email);
}
