package com.example.smartplanner.dto.admin;

import java.time.Instant;

public record UserAdminRow(
        Long id,
        String email,
        String name,
        String surname,
        String role,
        String profileImageUrl,
        Instant registeredAt
) {}
