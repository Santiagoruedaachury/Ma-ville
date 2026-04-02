package com.maville.dto;

import com.maville.model.TypeTravaux;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRequeteRequest(
    @NotBlank String titreDuTravail,
    String description,
    String dateDeDebut,
    @NotNull TypeTravaux typeDeTravaux
) {}
