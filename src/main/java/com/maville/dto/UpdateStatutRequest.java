package com.maville.dto;

import com.maville.model.StatutProjet;
import jakarta.validation.constraints.NotNull;

public record UpdateStatutRequest(
    @NotNull StatutProjet statutProjet
) {}
