package com.alexandre.doeflow.dto.auth;

import com.alexandre.doeflow.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password,
        UserRole role
) {
}
