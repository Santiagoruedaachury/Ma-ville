package com.maville.repository;

import com.maville.model.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByIntervenantId(Long intervenantId);
    List<Candidature> findByRequeteId(Long requeteId);
    Optional<Candidature> findByRequeteIdAndIntervenantId(Long requeteId, Long intervenantId);
}
