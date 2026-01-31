package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.request.LoginRequestDTO;
import com.personal.bookstoreapi.dto.request.RegisterRequestDTO;
import com.personal.bookstoreapi.dto.response.LoginResponseDTO;
import com.personal.bookstoreapi.dto.response.RegisterResponseDTO;
import com.personal.bookstoreapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody @Valid RegisterRequestDTO req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO req) {
        return authService.login(req);
    }
}