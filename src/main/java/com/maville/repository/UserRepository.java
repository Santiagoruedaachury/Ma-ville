package com.maville.repository;

import com.maville.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCourriel(String courriel);
    boolean existsByCourriel(String courriel);
}
