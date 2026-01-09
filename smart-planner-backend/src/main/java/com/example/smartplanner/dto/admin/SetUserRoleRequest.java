package com.example.smartplanner.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record SetUserRoleRequest(@NotBlank String role) {}
