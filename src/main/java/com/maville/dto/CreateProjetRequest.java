package com.maville.dto;

import com.maville.model.TypeTravaux;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProjetRequest(
    @NotBlank String titre,
    String description,
    @NotNull TypeTravaux typeTravaux,
    @NotBlank String dateDebut,
    String dateFin,
    @NotBlank String quartierAffecte
) {}
