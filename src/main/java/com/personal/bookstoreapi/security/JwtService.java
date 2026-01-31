package com.personal.bookstoreapi.security;

import com.personal.bookstoreapi.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {

    private final String secret;
    private final long accessMinutes;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-minutes}") long accessMinutes
    ) {
        this.secret = secret;
        this.accessMinutes = accessMinutes;
    }

    public String generateAccessToken(User user) {
        return buildToken(user, Duration.ofMinutes(accessMinutes));
    }

    private String buildToken(User user, Duration ttl) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);

        return Jwts.builder()
                   .subject(user.getEmail())
                   .claim("uid", user.getId())
                   .claim("name", user.getFullName())
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(exp))
                   .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                   .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                   .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }
}
