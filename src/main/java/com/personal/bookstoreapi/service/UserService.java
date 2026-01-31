package com.personal.bookstoreapi.service;

import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext()
                                                   .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Unauthenticated");
        }

        String email = auth.getName();

        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
    }
}