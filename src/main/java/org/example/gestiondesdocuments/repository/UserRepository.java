package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Utilisateur,Long> {
}
