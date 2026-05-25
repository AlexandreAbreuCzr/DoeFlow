package com.alexandre.doeflow.dto.user;

import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.model.user.UserRole;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role
) {

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
