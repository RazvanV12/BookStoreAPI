package com.personal.bookstoreapi.controller;

import com.personal.bookstoreapi.dto.request.LoginRequestDTO;
import com.personal.bookstoreapi.dto.request.RegisterRequestDTO;
import com.personal.bookstoreapi.dto.response.LoginResponseDTO;
import com.personal.bookstoreapi.dto.response.RegisterResponseDTO;
import com.personal.bookstoreapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    AuthService authService;

    @InjectMocks
    AuthController authController;

    @Test
    void register_delegatesToService_andReturnsResponse() {
        var req = new RegisterRequestDTO("u@x.com","pass","Name");
        var res = new RegisterResponseDTO("u@x.com","Name","tok");
        when(authService.register(req)).thenReturn(res);

        var out = authController.register(req);
        assertThat(out).isSameAs(res);
    }

    @Test
    void login_delegatesToService_andReturnsResponse() {
        var req = new LoginRequestDTO("u@x.com","pass");
        var res = new LoginResponseDTO("tok");
        when(authService.login(req)).thenReturn(res);

        var out = authController.login(req);
        assertThat(out).isSameAs(res);
    }
}
