package com.maville.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegisterResidentRequest(
    @NotBlank String nom,
    @NotBlank @Email String courriel,
    @NotBlank String password,
    @NotNull LocalDate dateNaissance,
    @NotBlank String adresseResidentielle
) {}
