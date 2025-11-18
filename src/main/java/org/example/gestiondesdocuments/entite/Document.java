package org.example.gestiondesdocuments.entite;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String numeroPiece;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeDocument type;

    @Column(length = 100)
    private String categorieComptable;

    @Column(nullable = false)
    private LocalDate datePiece;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(length = 255)
    private String fournisseur;

    @Column(nullable = false, length = 500)
    private String cheminFichier;

    @Column(nullable = false, length = 100)
    private String nomFichierOriginal;

    @Column(length = 100)
    private String typeFichier;

    @Column
    private Long tailleFichier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutDocument statut = StatutDocument.EN_ATTENTE;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(columnDefinition = "TEXT")
    private String commentaireComptable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_id", nullable = false)
    private Societe societe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploade_par_id", nullable = false)
    private Utilisateur uploadePar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valide_par_id")
    private Utilisateur validePar;

    @Column(nullable = false, length = 4)
    private String exerciceComptable;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public enum TypeDocument {
        FACTURE_ACHAT,
        FACTURE_VENTE,
        TICKET_CAISSE,
        RELEVE_BANCAIRE,
        AUTRE
    }

    public enum StatutDocument {
        EN_ATTENTE,
        VALIDE,
        REJETE
    }
}

