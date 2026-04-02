package com.maville.service;

import com.maville.dto.UpdateHoraireRequest;
import com.maville.model.Horaire;
import com.maville.model.JourDeLaSemaine;
import com.maville.repository.HoraireRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HoraireService {

    private final HoraireRepository horaireRepository;

    public HoraireService(HoraireRepository horaireRepository) {
        this.horaireRepository = horaireRepository;
    }

    public List<Horaire> getByResident(Long residentId) {
        return horaireRepository.findByResidentId(residentId);
    }

    public Horaire update(Long horaireId, UpdateHoraireRequest request, Long residentId) {
        Horaire horaire = horaireRepository.findById(horaireId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horaire non trouvé"));

        if (!horaire.getResident().getId().equals(residentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez modifier que vos propres horaires");
        }

        horaire.setHeureDebut(request.heureDebut());
        horaire.setHeureFin(request.heureFin());
        return horaireRepository.save(horaire);
    }
}
