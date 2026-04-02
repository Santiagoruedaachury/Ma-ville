package com.maville.repository;

import com.maville.model.Projet;
import com.maville.model.StatutProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjetRepository extends JpaRepository<Projet, Long> {
    List<Projet> findByQuartierAffecteIgnoreCase(String quartier);
    List<Projet> findByStatutProjet(StatutProjet statut);
    List<Projet> findByIntervenantId(Long intervenantId);
}
