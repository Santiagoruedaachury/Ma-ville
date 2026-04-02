package com.maville.controller;

import com.maville.dto.CreateRequeteRequest;
import com.maville.model.Requete;
import com.maville.model.Resident;
import com.maville.model.User;
import com.maville.service.RequeteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requetes")
public class RequeteController {

    private final RequeteService requeteService;

    public RequeteController(RequeteService requeteService) {
        this.requeteService = requeteService;
    }

    @GetMapping
    public List<Requete> getAll(@RequestParam(required = false) Boolean ouvert) {
        if (Boolean.TRUE.equals(ouvert)) return requeteService.getOpen();
        return requeteService.getAll();
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('RESIDENT')")
    public List<Requete> getMyRequetes(@AuthenticationPrincipal User user) {
        return requeteService.getByResident(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<Requete> create(@Valid @RequestBody CreateRequeteRequest request,
                                          @AuthenticationPrincipal User user) {
        Requete requete = requeteService.create(request, (Resident) user);
        return ResponseEntity.status(HttpStatus.CREATED).body(requete);
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('RESIDENT')")
    public Requete close(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return requeteService.close(id, (Resident) user);
    }
}
