package com.personal.bookstoreapi.security;

import com.personal.bookstoreapi.domain.entity.User;
import com.personal.bookstoreapi.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        System.out.println("AUTH HEADER = " + request.getHeader("Authorization"));
        System.out.println("PATH = " + request.getRequestURI());

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtService.parseClaims(token);
            String email = claims.getSubject();

            if (email != null && SecurityContextHolder.getContext()
                                                      .getAuthentication() == null) {

                User user = userRepository.findByEmail(email)
                                          .orElse(null);

                if (user != null) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            Collections.emptyList()
                    );

                    auth.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                            .buildDetails(request));

                    SecurityContextHolder.getContext()
                                         .setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            System.out.println("JWT ERROR: " + e.getClass()
                                                .getSimpleName() + " - " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
