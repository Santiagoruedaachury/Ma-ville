package com.maville.service;

import com.maville.dto.CreateRequeteRequest;
import com.maville.model.Requete;
import com.maville.model.Resident;
import com.maville.repository.RequeteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RequeteService {

    private final RequeteRepository requeteRepository;

    public RequeteService(RequeteRepository requeteRepository) {
        this.requeteRepository = requeteRepository;
    }

    public List<Requete> getAll() {
        return requeteRepository.findAll();
    }

    public List<Requete> getOpen() {
        return requeteRepository.findByOuvertTrue();
    }

    public List<Requete> getByResident(Long residentId) {
        return requeteRepository.findByProprietaireId(residentId);
    }

    public Requete create(CreateRequeteRequest request, Resident resident) {
        Requete requete = new Requete();
        requete.setTitreDuTravail(request.titreDuTravail());
        requete.setDescription(request.description());
        requete.setDateDeDebut(request.dateDeDebut());
        requete.setTypeDeTravaux(request.typeDeTravaux());
        requete.setOuvert(true);
        requete.setProprietaire(resident);
        return requeteRepository.save(requete);
    }

    public Requete close(Long requeteId, Resident resident) {
        Requete requete = requeteRepository.findById(requeteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requête non trouvée"));

        if (!requete.getProprietaire().getId().equals(resident.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez fermer que vos propres requêtes");
        }

        requete.setOuvert(false);
        return requeteRepository.save(requete);
    }
}
