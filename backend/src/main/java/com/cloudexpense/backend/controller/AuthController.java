package com.cloudexpense.backend.controller;

import com.cloudexpense.backend.entity.user;
import com.cloudexpense.backend.repository.UserRepository;
import com.cloudexpense.backend.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.cloudexpense.backend.dto.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) {

        this.authenticationManager =
                authenticationManager;

        this.userRepository =
                userRepository;

        this.jwtService =
                jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        user user =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow();

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole().name()
                );

        return ResponseEntity.ok(
                new LoginResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        token
                )
        );
    }

    public record LoginResponse(
            Long id,
            String name,
            String email,
            user.Role role,
            String token
    ) {
    }
}