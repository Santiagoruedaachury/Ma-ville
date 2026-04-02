package com.maville.service;

import com.maville.dto.AuthResponse;
import com.maville.dto.LoginRequest;
import com.maville.dto.RegisterIntervenantRequest;
import com.maville.dto.RegisterResidentRequest;
import com.maville.model.*;
import com.maville.repository.HoraireRepository;
import com.maville.repository.IntervenantRepository;
import com.maville.repository.ResidentRepository;
import com.maville.repository.UserRepository;
import com.maville.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ResidentRepository residentRepository;
    private final IntervenantRepository intervenantRepository;
    private final HoraireRepository horaireRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, ResidentRepository residentRepository,
                       IntervenantRepository intervenantRepository, HoraireRepository horaireRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.residentRepository = residentRepository;
        this.intervenantRepository = intervenantRepository;
        this.horaireRepository = horaireRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByCourriel(request.courriel())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Courriel ou mot de passe invalide"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Courriel ou mot de passe invalide");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getCourriel(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getNom(), user.getId());
    }

    public AuthResponse registerResident(RegisterResidentRequest request) {
        if (userRepository.existsByCourriel(request.courriel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte avec ce courriel existe déjà");
        }

        Resident resident = new Resident();
        resident.setNom(request.nom());
        resident.setCourriel(request.courriel());
        resident.setPassword(passwordEncoder.encode(request.password()));
        resident.setRole(Role.RESIDENT);
        resident.setDateNaissance(request.dateNaissance());
        resident.setAdresseResidentielle(request.adresseResidentielle());

        resident = residentRepository.save(resident);

        // Initialize default schedule for each day
        for (JourDeLaSemaine jour : JourDeLaSemaine.values()) {
            Horaire horaire = new Horaire();
            horaire.setJourDeLaSemaine(jour);
            horaire.setHeureDebut("Pas spécifié");
            horaire.setHeureFin("Pas spécifié");
            horaire.setResident(resident);
            horaireRepository.save(horaire);
        }

        String token = jwtUtil.generateToken(resident.getId(), resident.getCourriel(), Role.RESIDENT.name());
        return new AuthResponse(token, Role.RESIDENT.name(), resident.getNom(), resident.getId());
    }

    public AuthResponse registerIntervenant(RegisterIntervenantRequest request) {
        if (userRepository.existsByCourriel(request.courriel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte avec ce courriel existe déjà");
        }
        if (intervenantRepository.existsByIdentifiantVille(request.identifiantVille())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet identifiant de ville est déjà utilisé");
        }

        Intervenant intervenant = new Intervenant();
        intervenant.setNom(request.nom());
        intervenant.setCourriel(request.courriel());
        intervenant.setPassword(passwordEncoder.encode(request.password()));
        intervenant.setRole(Role.INTERVENANT);
        intervenant.setType(request.type());
        intervenant.setIdentifiantVille(request.identifiantVille());

        intervenant = intervenantRepository.save(intervenant);

        String token = jwtUtil.generateToken(intervenant.getId(), intervenant.getCourriel(), Role.INTERVENANT.name());
        return new AuthResponse(token, Role.INTERVENANT.name(), intervenant.getNom(), intervenant.getId());
    }
}
