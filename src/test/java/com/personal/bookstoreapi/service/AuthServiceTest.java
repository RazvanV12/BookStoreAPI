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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    AuthService authService;

    @BeforeEach
    void setup() {
        // use same bounds as application.properties
        authService = new AuthService(userRepository, passwordEncoder, jwtService, 4, 20);
    }

    @Nested
    class Register {

        @Test
        void happy_savesUserAndReturnsRegisterResponse() {
            var req = new RegisterRequestDTO("Test@Example.com","secret"," John Doe ");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode("secret")).thenReturn("hashed");

            User saved = User.builder()
                    .id(10L)
                    .email("test@example.com")
                    .fullName("John Doe")
                    .passwordHash("hashed")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(userRepository.save(any())).thenReturn(saved);
            when(jwtService.generateAccessToken(any())).thenReturn("token-123");

            RegisterResponseDTO res = authService.register(req);

            assertThat(res.email()).isEqualTo("test@example.com");
            assertThat(res.fullName()).isEqualTo("John Doe");
            assertThat(res.accessToken()).isEqualTo("token-123");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User toSave = captor.getValue();
            assertThat(toSave.getEmail()).isEqualTo("test@example.com");
            assertThat(toSave.getFullName()).isEqualTo("John Doe");
            assertThat(toSave.getPasswordHash()).isEqualTo("hashed");
        }

        @Test
        void unhappy_passwordNull_throwsIllegalArgumentException() {
            var req = new RegisterRequestDTO("a@a.com", null, "Name");
            when(userRepository.existsByEmail("a@a.com")).thenReturn(false);

            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password is required");
        }

        @Test
        void unhappy_emailNull_throwsIllegalArgumentException() {
            var req = new RegisterRequestDTO(null, "abcd", "Name");
            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email is required");
        }

        @Test
        void happy_passwordAtBounds_minAndMaxAllowed() {
            // min length 4
            var reqMin = new RegisterRequestDTO("x@x.com", "abcd", "Name");
            when(userRepository.existsByEmail("x@x.com")).thenReturn(false);
            when(passwordEncoder.encode("abcd")).thenReturn("h1");
            when(userRepository.save(any())).thenReturn(User.builder().id(1L).email("x@x.com").fullName("Name").passwordHash("h1").build());
            when(jwtService.generateAccessToken(any())).thenReturn("t");
            authService.register(reqMin);

            // max length 20
            String longPass = "p".repeat(20);
            var reqMax = new RegisterRequestDTO("y@y.com", longPass, "Name");
            when(userRepository.existsByEmail("y@y.com")).thenReturn(false);
            when(passwordEncoder.encode(longPass)).thenReturn("h2");
            when(userRepository.save(any())).thenReturn(User.builder().id(2L).email("y@y.com").fullName("Name").passwordHash("h2").build());
            when(jwtService.generateAccessToken(any())).thenReturn("t2");
            authService.register(reqMax);
        }

        @Test
        void unhappy_emailAlreadyUsed_throwsConflictException() {
            var req = new RegisterRequestDTO("a@a.com","pass","Name");
            when(userRepository.existsByEmail("a@a.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Email already in use");

            verify(userRepository, never()).save(any());
        }

        @Test
        void unhappy_passwordTooShort_throwsIllegalArgumentException() {
            var req = new RegisterRequestDTO("a@a.com","x","Name");
            when(userRepository.existsByEmail("a@a.com")).thenReturn(false);

            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password length");

            verify(userRepository, never()).save(any());
        }

        @Test
        void unhappy_passwordTooLong_throwsIllegalArgumentException() {
            String tooLong = "p".repeat(21);
            var req = new RegisterRequestDTO("a@a.com", tooLong, "Name");
            when(userRepository.existsByEmail("a@a.com")).thenReturn(false);

            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password length");
        }

        @Test
        void unhappy_missingFullName_throwsIllegalArgumentException() {
            var req = new RegisterRequestDTO("a@a.com","validpass","   ");
            when(userRepository.existsByEmail("a@a.com")).thenReturn(false);

            assertThatThrownBy(() -> authService.register(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Full name is required");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class Login {

        @Test
        void happy_validCredentials_returnsLoginResponse() {
            var req = new LoginRequestDTO(" u@x.com ","pwd");
            User user = User.builder().id(5L).email("u@x.com").passwordHash("h").build();
            when(userRepository.findByEmail("u@x.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("pwd","h")).thenReturn(true);
            when(jwtService.generateAccessToken(user)).thenReturn("tok");

            LoginResponseDTO res = authService.login(req);
            assertThat(res.accessToken()).isEqualTo("tok");
        }

        @Test
        void unhappy_nullEmail_throwsIllegalArgumentException() {
            var req = new LoginRequestDTO(null, "pwd");
            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email is required");
        }

        @Test
        void unhappy_userNotFound_throwsUnauthorizedException() {
            var req = new LoginRequestDTO("not@found.com","pwd");
            when(userRepository.findByEmail("not@found.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("Invalid credentials");
        }

        @Test
        void unhappy_incorrectPassword_throwsIllegalArgumentException() {
            var req = new LoginRequestDTO("u@x.com","pwd");
            User user = User.builder().id(5L).email("u@x.com").passwordHash("h").build();
            when(userRepository.findByEmail("u@x.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("pwd","h")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid credentials");
        }
    }
}
