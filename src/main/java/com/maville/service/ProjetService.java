package com.maville.service;

import com.maville.dto.CreateProjetRequest;
import com.maville.dto.UpdateStatutRequest;
import com.maville.model.Intervenant;
import com.maville.model.Projet;
import com.maville.model.StatutProjet;
import com.maville.repository.ProjetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final NotificationService notificationService;

    public ProjetService(ProjetRepository projetRepository, NotificationService notificationService) {
        this.projetRepository = projetRepository;
        this.notificationService = notificationService;
    }

    public List<Projet> getAll() {
        return projetRepository.findAll();
    }

    public List<Projet> getByQuartier(String quartier) {
        return projetRepository.findByQuartierAffecteIgnoreCase(quartier);
    }

    public List<Projet> getByStatut(StatutProjet statut) {
        return projetRepository.findByStatutProjet(statut);
    }

    public List<Projet> getByIntervenant(Long intervenantId) {
        return projetRepository.findByIntervenantId(intervenantId);
    }

    public Projet create(CreateProjetRequest request, Intervenant intervenant) {
        Projet projet = new Projet();
        projet.setTitre(request.titre());
        projet.setDescription(request.description());
        projet.setTypeTravaux(request.typeTravaux());
        projet.setDateDebut(request.dateDebut());
        projet.setDateFin(request.dateFin());
        projet.setQuartierAffecte(request.quartierAffecte());
        projet.setIntervenant(intervenant);

        projet = projetRepository.save(projet);

        notificationService.notifyResidentsByQuartier(
                request.quartierAffecte(),
                "Nouveau projet « " + request.titre() + " » dans votre quartier " + request.quartierAffecte()
        );

        return projet;
    }

    public Projet updateStatut(Long projetId, UpdateStatutRequest request, Intervenant intervenant) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet non trouvé"));

        if (!projet.getIntervenant().getId().equals(intervenant.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez modifier que vos propres projets");
        }

        projet.setStatutProjet(request.statutProjet());
        projet = projetRepository.save(projet);

        notificationService.notifyResidentsByQuartier(
                projet.getQuartierAffecte(),
                "Le projet « " + projet.getTitre() + " » est maintenant " + request.statutProjet()
        );

        return projet;
    }
}
