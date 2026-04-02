package com.maville.controller;

import com.maville.model.Candidature;
import com.maville.model.Intervenant;
import com.maville.model.User;
import com.maville.service.CandidatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    private final CandidatureService candidatureService;

    public CandidatureController(CandidatureService candidatureService) {
        this.candidatureService = candidatureService;
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('INTERVENANT')")
    public List<Candidature> getMyCandidatures(@AuthenticationPrincipal User user) {
        return candidatureService.getByIntervenant(user.getId());
    }

    @GetMapping("/requete/{requeteId}")
    public List<Candidature> getByRequete(@PathVariable Long requeteId) {
        return candidatureService.getByRequete(requeteId);
    }

    @PostMapping("/requete/{requeteId}")
    @PreAuthorize("hasRole('INTERVENANT')")
    public ResponseEntity<Candidature> submit(@PathVariable Long requeteId,
                                              @AuthenticationPrincipal User user) {
        Candidature candidature = candidatureService.submit(requeteId, (Intervenant) user);
        return ResponseEntity.status(HttpStatus.CREATED).body(candidature);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INTERVENANT')")
    public ResponseEntity<Void> withdraw(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        candidatureService.withdraw(id, (Intervenant) user);
        return ResponseEntity.noContent().build();
    }
}
