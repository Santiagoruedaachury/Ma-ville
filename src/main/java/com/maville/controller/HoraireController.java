package com.maville.controller;

import com.maville.dto.UpdateHoraireRequest;
import com.maville.model.Horaire;
import com.maville.model.User;
import com.maville.service.HoraireService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horaires")
@PreAuthorize("hasRole('RESIDENT')")
public class HoraireController {

    private final HoraireService horaireService;

    public HoraireController(HoraireService horaireService) {
        this.horaireService = horaireService;
    }

    @GetMapping
    public List<Horaire> getMyHoraires(@AuthenticationPrincipal User user) {
        return horaireService.getByResident(user.getId());
    }

    @PatchMapping("/{id}")
    public Horaire update(@PathVariable Long id,
                          @Valid @RequestBody UpdateHoraireRequest request,
                          @AuthenticationPrincipal User user) {
        return horaireService.update(id, request, user.getId());
    }
}
