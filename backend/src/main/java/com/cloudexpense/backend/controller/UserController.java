package com.cloudexpense.backend.controller;

import com.cloudexpense.backend.dto.RegisterRequest;
import com.cloudexpense.backend.entity.user;
import com.cloudexpense.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        if (userService.emailExists(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        user newUser = new user();

        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());

        // Public registration always creates an EMPLOYEE.
        newUser.setRole(user.Role.EMPLOYEE);

        user savedUser = userService.createUser(newUser);

        // Never return the password.
        savedUser.setPassword(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }
}