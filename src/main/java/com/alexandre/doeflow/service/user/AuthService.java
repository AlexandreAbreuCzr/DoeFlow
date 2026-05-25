package com.alexandre.doeflow.service.user;

import com.alexandre.doeflow.dto.auth.AuthResponseDTO;
import com.alexandre.doeflow.dto.auth.LoginRequestDTO;
import com.alexandre.doeflow.dto.auth.RegisterRequestDTO;
import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.model.user.UserRole;
import com.alexandre.doeflow.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        validateRegister(request);

        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ja cadastrado");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(resolvePublicRole(request.role()));

        User savedUser = userRepository.save(user);
        String token = tokenService.generateToken(savedUser);
        return AuthResponseDTO.from(savedUser, token, tokenService.getExpirationSeconds());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        validateLogin(request);

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                normalizeEmail(request.email()),
                request.password()
        );
        var authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();
        String token = tokenService.generateToken(user);
        return AuthResponseDTO.from(user, token, tokenService.getExpirationSeconds());
    }

    private void validateRegister(RegisterRequestDTO request) {
        if (request == null || isBlank(request.name()) || isBlank(request.email()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, e-mail e senha sao obrigatorios");
        }
    }

    private void validateLogin(LoginRequestDTO request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail e senha sao obrigatorios");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserRole resolvePublicRole(UserRole role) {
        if (role == null) {
            return UserRole.DONOR;
        }

        if (role == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin nao pode ser criado pelo cadastro publico");
        }

        return role;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
