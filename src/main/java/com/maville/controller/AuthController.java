package com.maville.controller;

import com.maville.dto.AuthResponse;
import com.maville.dto.LoginRequest;
import com.maville.dto.RegisterIntervenantRequest;
import com.maville.dto.RegisterResidentRequest;
import com.maville.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/resident")
    public ResponseEntity<AuthResponse> registerResident(@Valid @RequestBody RegisterResidentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerResident(request));
    }

    @PostMapping("/register/intervenant")
    public ResponseEntity<AuthResponse> registerIntervenant(@Valid @RequestBody RegisterIntervenantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerIntervenant(request));
    }
}
