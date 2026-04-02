package com.maville.repository;

import com.maville.model.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HoraireRepository extends JpaRepository<Horaire, Long> {
    List<Horaire> findByResidentId(Long residentId);
}
