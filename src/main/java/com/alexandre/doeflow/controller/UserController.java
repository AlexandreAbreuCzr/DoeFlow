package com.alexandre.doeflow.controller;

import com.alexandre.doeflow.dto.user.UserResponseDTO;
import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(UserResponseDTO::from)
                .toList();

        return ResponseEntity.ok(users);
    }
}
