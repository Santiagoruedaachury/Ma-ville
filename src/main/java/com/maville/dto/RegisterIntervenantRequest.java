package com.maville.dto;

import com.maville.model.IntervenantType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterIntervenantRequest(
    @NotBlank String nom,
    @NotBlank @Email String courriel,
    @NotBlank String password,
    @NotNull IntervenantType type,
    @NotNull Integer identifiantVille
) {}
