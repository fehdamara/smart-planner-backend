package com.example.smartplanner.dto.auth;

import java.time.Instant;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Instant refreshExpiresAt,
        Long id,
        String email,
        String name,
        String surname,
        String role,
        String profileImageUrl
) {}
