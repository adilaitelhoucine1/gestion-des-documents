package org.example.gestiondesdocuments.dto.Documents;

import org.example.gestiondesdocuments.entite.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentUploadResponse(
        Long id,
        String numeroPiece,
        Document.TypeDocument type,
        String categorieComptable,
        LocalDate datePiece,
        BigDecimal montant,
        String fournisseur,
        String nomFichierOriginal,
        String typeFichier,
        Long tailleFichier,
        Document.StatutDocument statut,
        String exerciceComptable,
        LocalDateTime dateCreation,
        String message
) {
}
