package com.maville.repository;

import com.maville.model.Intervenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntervenantRepository extends JpaRepository<Intervenant, Long> {
    boolean existsByIdentifiantVille(Integer identifiantVille);
}
