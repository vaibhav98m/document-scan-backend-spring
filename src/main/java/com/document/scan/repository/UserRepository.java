package com.document.scan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.document.scan.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
