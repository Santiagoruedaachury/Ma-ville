package com.maville.dto;

public record AuthResponse(
    String token,
    String role,
    String nom,
    Long userId
) {}
