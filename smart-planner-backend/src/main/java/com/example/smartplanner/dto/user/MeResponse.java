package com.example.smartplanner.dto.user;

import java.time.Instant;

public record MeResponse(
        Long id,
        String email,
        String name,
        String surname,
        String role,
        String profileImageUrl,
        Instant registeredAt
) {}
