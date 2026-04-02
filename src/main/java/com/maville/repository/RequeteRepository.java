package com.maville.repository;

import com.maville.model.Requete;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequeteRepository extends JpaRepository<Requete, Long> {
    List<Requete> findByProprietaireId(Long residentId);
    List<Requete> findByOuvertTrue();
}
