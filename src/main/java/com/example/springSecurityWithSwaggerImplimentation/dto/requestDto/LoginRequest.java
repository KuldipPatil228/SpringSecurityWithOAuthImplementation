package com.example.springSecurityWithSwaggerImplimentation.dto.requestDto;

import com.example.springSecurityWithSwaggerImplimentation.entity.Role;
import jakarta.validation.constraints.NotBlank;



public record LoginRequest(
        @NotBlank(message = "Username must not be empty")
         String username,

        @NotBlank(message = "Password must not be empty")
        String password,

        Role role
) {
}
