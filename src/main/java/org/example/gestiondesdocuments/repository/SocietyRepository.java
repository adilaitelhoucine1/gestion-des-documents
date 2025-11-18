package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Societe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocietyRepository extends JpaRepository<Societe,Long> {
}