package com.cloudexpense.backend.repository;

import com.cloudexpense.backend.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<user, Long> {

    Optional<user> findByEmail(String email);

    boolean existsByEmail(String email);
}