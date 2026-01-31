package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.dto.request.LoginRequestDTO;
import com.personal.bookstoreapi.dto.request.RegisterRequestDTO;
import com.personal.bookstoreapi.dto.response.LoginResponseDTO;
import com.personal.bookstoreapi.dto.response.RegisterResponseDTO;
import com.personal.bookstoreapi.exception.ConflictException;
import com.personal.bookstoreapi.exception.UnauthorizedException;
import com.personal.bookstoreapi.repository.UserRepository;
import com.personal.bookstoreapi.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final int minPass;
    private final int maxPass;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${security.password.min-length}") int minPass,
            @Value("${security.password.max-length}") int maxPass
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.minPass = minPass;
        this.maxPass = maxPass;
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        String email = normalizeEmail(registerRequestDTO.email());

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already in use");
        }

        validatePassword(registerRequestDTO.password());
        if (registerRequestDTO.fullName() == null || registerRequestDTO.fullName()
                                         .isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        User user = User.builder()
                        .email(email)
                        .passwordHash(passwordEncoder.encode(registerRequestDTO.password()))
                        .fullName(registerRequestDTO.fullName()
                                     .trim())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);

        return new RegisterResponseDTO(user.getEmail(), user.getFullName(), accessToken);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String email = normalizeEmail(loginRequestDTO.email());

        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String access = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(access);
    }

    private void validatePassword(String password) {
        if (password == null) throw new IllegalArgumentException("Password is required");
        int len = password.length();
        if (len < minPass || len > maxPass) {
            throw new IllegalArgumentException("Password length must be between " + minPass + " and " + maxPass);
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        return email.trim()
                    .toLowerCase();
    }
}