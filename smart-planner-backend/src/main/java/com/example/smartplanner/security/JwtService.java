package com.example.smartplanner.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class JwtService {

    private final Key key;
    private final long accessExpMinutes;
    private final long refreshExpDays;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.accessExpMinutes}") long accessExpMinutes,
            @Value("${app.jwt.refreshExpDays}") long refreshExpDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMinutes = accessExpMinutes;
        this.refreshExpDays = refreshExpDays;
    }

    public record JwtPair(String accessToken, String refreshToken, Instant refreshExpiresAt, String refreshJti) {}

    public JwtPair generatePair(Long userId, String email, String role) {
        Instant now = Instant.now();
        Instant accessExp = now.plus(accessExpMinutes, ChronoUnit.MINUTES);
        Instant refreshExp = now.plus(refreshExpDays, ChronoUnit.DAYS);

        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        String access = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(accessJti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(accessExp))
                .addClaims(Map.of("email", email, "role", role, "typ", "access"))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refresh = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(refreshJti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(refreshExp))
                .addClaims(Map.of("email", email, "role", role, "typ", "refresh"))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new JwtPair(access, refresh, refreshExp, refreshJti);
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public boolean isAccessToken(Claims c) { return "access".equals(c.get("typ", String.class)); }
    public boolean isRefreshToken(Claims c) { return "refresh".equals(c.get("typ", String.class)); }
}
