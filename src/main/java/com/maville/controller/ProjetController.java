package com.maville.controller;

import com.maville.dto.CreateProjetRequest;
import com.maville.dto.UpdateStatutRequest;
import com.maville.model.Intervenant;
import com.maville.model.Projet;
import com.maville.model.StatutProjet;
import com.maville.model.User;
import com.maville.service.ProjetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
public class ProjetController {

    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @GetMapping
    public List<Projet> getAll(@RequestParam(required = false) String quartier,
                               @RequestParam(required = false) StatutProjet statut) {
        if (quartier != null) return projetService.getByQuartier(quartier);
        if (statut != null) return projetService.getByStatut(statut);
        return projetService.getAll();
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('INTERVENANT')")
    public List<Projet> getMyProjets(@AuthenticationPrincipal User user) {
        return projetService.getByIntervenant(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('INTERVENANT')")
    public ResponseEntity<Projet> create(@Valid @RequestBody CreateProjetRequest request,
                                         @AuthenticationPrincipal User user) {
        Projet projet = projetService.create(request, (Intervenant) user);
        return ResponseEntity.status(HttpStatus.CREATED).body(projet);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('INTERVENANT')")
    public Projet updateStatut(@PathVariable Long id,
                               @Valid @RequestBody UpdateStatutRequest request,
                               @AuthenticationPrincipal User user) {
        return projetService.updateStatut(id, request, (Intervenant) user);
    }
}
