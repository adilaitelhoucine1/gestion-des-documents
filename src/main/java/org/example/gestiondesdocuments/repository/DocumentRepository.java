package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.example.gestiondesdocuments.entite.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document,Long> {

    @Query("select d from Document d where d.societe.id=:societyId and YEAR(d.datePiece)=:exercice")
    List<DocumentUploadResponse> getDocsByExercice(@Param("societyId") Long societyId, @Param("exercice") int exercice);
}
