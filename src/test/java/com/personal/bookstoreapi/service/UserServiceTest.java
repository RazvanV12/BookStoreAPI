package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @AfterEach
    void tearDown() {
        TestSecurityUtil.clearAuthentication();
    }

    @Nested
    class GetCurrentUser {
        @Test
        void happy_returnsUserWhenAuthenticated() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getName()).thenReturn("a@b.com");
            TestSecurityUtil.setAuthentication(auth);

            User u = User.builder().id(2L).email("a@b.com").fullName("X").build();
            when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));

            User res = userService.getCurrentUser();
            assertThat(res.getId()).isEqualTo(2L);
        }

        @Test
        void unhappy_authPresentButNotAuthenticated_throwsSecurityException() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(false);
            TestSecurityUtil.setAuthentication(auth);

            assertThatThrownBy(() -> userService.getCurrentUser()).isInstanceOf(SecurityException.class).hasMessageContaining("Unauthenticated");
        }

        @Test
        void unhappy_noAuthentication_throwsSecurityException() {
            TestSecurityUtil.clearAuthentication();
            assertThatThrownBy(() -> userService.getCurrentUser()).isInstanceOf(SecurityException.class).hasMessageContaining("Unauthenticated");
        }

        @Test
        void unhappy_userNotFound_throwsIllegalArgumentException() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getName()).thenReturn("no@user");
            TestSecurityUtil.setAuthentication(auth);
            when(userRepository.findByEmail("no@user")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getCurrentUser()).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("User not found");
        }
    }
}
