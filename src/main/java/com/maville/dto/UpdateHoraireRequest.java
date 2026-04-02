package com.maville.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateHoraireRequest(
    @NotBlank String heureDebut,
    @NotBlank String heureFin
) {}
