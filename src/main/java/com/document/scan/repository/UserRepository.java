package com.document.scan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.document.scan.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);

}
