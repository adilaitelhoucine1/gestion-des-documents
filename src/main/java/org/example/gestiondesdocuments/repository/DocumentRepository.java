package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
}
