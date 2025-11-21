package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Societe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocietyRepository extends JpaRepository<Societe,Long> {

    Optional<Societe> existsDistinctByEmailContact(String email);
}