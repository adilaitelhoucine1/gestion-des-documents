package org.example.gestiondesdocuments.entite;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "societes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Societe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String raisonSociale;

    @Column(nullable = false, unique = true, length = 15)
    private String ice;

    @Column(length = 500)
    private String adresse;

    @Column(length = 20)
    private String telephone;

    @Column(nullable = false, unique = true, length = 100)
    private String emailContact;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "societe", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Utilisateur> utilisateurs = new HashSet<>();

    @OneToMany(mappedBy = "societe", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Document> documents = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}

