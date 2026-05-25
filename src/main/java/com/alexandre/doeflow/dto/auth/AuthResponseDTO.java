package com.alexandre.doeflow.dto.auth;

import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.model.user.UserRole;

import java.util.UUID;

public record AuthResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role,
        String accessToken,
        String tokenType,
        long expiresIn
) {

    public static AuthResponseDTO from(User user, String accessToken, long expiresIn) {
        return new AuthResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessToken,
                "Bearer",
                expiresIn
        );
    }
}
