package com.example.smartplanner.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileImageRequest(@NotBlank String profileImageUrl) {}
