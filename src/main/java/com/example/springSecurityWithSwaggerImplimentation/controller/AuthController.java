package com.example.springSecurityWithSwaggerImplimentation.controller;

import com.example.springSecurityWithSwaggerImplimentation.entity.*;
import com.example.springSecurityWithSwaggerImplimentation.repo.UserRepository;
import com.example.springSecurityWithSwaggerImplimentation.dto.requestDto.LoginRequest;
import com.example.springSecurityWithSwaggerImplimentation.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;// Now injected from EncoderConfig


    // ===== REGISTER USER =====
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        if (user.getUsername() == null || user.getUsername().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Username and password cannot be empty");
        }

        if (user.getRole() == null) user.setRole(Role.USER); // default role

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    // ===== LOGIN USER =====
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 1️⃣ Fetch user from DB
        User user = userRepository.findByUsername(loginRequest.username())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // 2️⃣ Validate password
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // 3️⃣ Validate role exists
        if (user.getRole() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User has no valid role assigned");
        }

        // 4️⃣ Optional: check if login request specifies required role
        if (loginRequest.role() != null && !user.getRole().equals(loginRequest.role())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User does not have the required role");
        }

        // 5️⃣ Generate JWT token with username + role
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // 6️⃣ Return token as JSON
        return ResponseEntity.ok(token);
    }
}

