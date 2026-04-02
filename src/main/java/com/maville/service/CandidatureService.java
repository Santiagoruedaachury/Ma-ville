package com.maville.service;

import com.maville.model.Candidature;
import com.maville.model.Intervenant;
import com.maville.model.Requete;
import com.maville.repository.CandidatureRepository;
import com.maville.repository.RequeteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final RequeteRepository requeteRepository;
    private final NotificationService notificationService;

    public CandidatureService(CandidatureRepository candidatureRepository,
                              RequeteRepository requeteRepository,
                              NotificationService notificationService) {
        this.candidatureRepository = candidatureRepository;
        this.requeteRepository = requeteRepository;
        this.notificationService = notificationService;
    }

    public List<Candidature> getByIntervenant(Long intervenantId) {
        return candidatureRepository.findByIntervenantId(intervenantId);
    }

    public List<Candidature> getByRequete(Long requeteId) {
        return candidatureRepository.findByRequeteId(requeteId);
    }

    public Candidature submit(Long requeteId, Intervenant intervenant) {
        Requete requete = requeteRepository.findById(requeteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requête non trouvée"));

        if (!requete.isOuvert()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette requête est fermée");
        }

        if (candidatureRepository.findByRequeteIdAndIntervenantId(requeteId, intervenant.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà soumis une candidature pour cette requête");
        }

        Candidature candidature = new Candidature();
        candidature.setRequete(requete);
        candidature.setIntervenant(intervenant);
        candidature = candidatureRepository.save(candidature);

        notificationService.notifyUser(
                requete.getProprietaire().getId(),
                "Nouvelle candidature de " + intervenant.getNom() + " pour votre requête « " + requete.getTitreDuTravail() + " »"
        );

        return candidature;
    }

    public void withdraw(Long candidatureId, Intervenant intervenant) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidature non trouvée"));

        if (!candidature.getIntervenant().getId().equals(intervenant.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez retirer que vos propres candidatures");
        }

        candidatureRepository.delete(candidature);
    }
}
