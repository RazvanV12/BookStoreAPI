package com.personal.bookstoreapi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public final class TestSecurityUtil {

    private TestSecurityUtil() {}

    public static void setAuthentication(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
