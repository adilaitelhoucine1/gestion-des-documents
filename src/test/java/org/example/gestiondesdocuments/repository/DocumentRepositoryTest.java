package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Document;
import org.example.gestiondesdocuments.entite.Societe;
import org.example.gestiondesdocuments.entite.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Societe societe;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        // Créer une société de test
        societe = Societe.builder()
                .raisonSociale("Test Company")
                .ice("123456789012345")
                .emailContact("test@company.com")
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();
        societe = entityManager.persistAndFlush(societe);

        // Créer un utilisateur de test
        utilisateur = Utilisateur.builder()
                .email("user@test.com")
                .motDePasse("password")
                .nomComplet("Test User")
                .societe(societe)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();
        utilisateur = entityManager.persistAndFlush(utilisateur);
    }

    @Test
    void testSaveDocument() {
        // Créer un document
        Document document = Document.builder()
                .numeroPiece("DOC-001")
                .type(Document.TypeDocument.FACTURE_ACHAT)
                .datePiece(LocalDate.of(2024, 1, 15))
                .montant(new BigDecimal("1500.50"))
                .fournisseur("Fournisseur Test")
                .cheminFichier("/uploads/doc001.pdf")
                .nomFichierOriginal("facture.pdf")
                .typeFichier("application/pdf")
                .tailleFichier(12345L)
                .statut(Document.StatutDocument.EN_ATTENTE)
                .societe(societe)
                .uploadePar(utilisateur)
                .exerciceComptable("2024")
                .dateCreation(LocalDateTime.now())
                .build();

        // Sauvegarder le document
        Document savedDocument = documentRepository.save(document);

        // Vérifications
        assertThat(savedDocument).isNotNull();
        assertThat(savedDocument.getId()).isNotNull();
        assertThat(savedDocument.getNumeroPiece()).isEqualTo("DOC-001");
        assertThat(savedDocument.getMontant()).isEqualByComparingTo(new BigDecimal("1500.50"));
    }

    @Test
    void testFindById() {
        // Créer et sauvegarder un document
        Document document = createTestDocument("DOC-002", 2024);
        document = entityManager.persistAndFlush(document);

        // Rechercher le document par ID
        Optional<Document> foundDocument = documentRepository.findById(document.getId());

        // Vérifications
        assertThat(foundDocument).isPresent();
        assertThat(foundDocument.get().getNumeroPiece()).isEqualTo("DOC-002");
    }

    @Test
    void testFindAll() {
        // Créer plusieurs documents
        Document doc1 = createTestDocument("DOC-003", 2024);
        Document doc2 = createTestDocument("DOC-004", 2024);
        entityManager.persist(doc1);
        entityManager.persist(doc2);
        entityManager.flush();

        // Récupérer tous les documents
        List<Document> documents = documentRepository.findAll();

        // Vérifications
        assertThat(documents).isNotEmpty();
        assertThat(documents.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testFindDocumentsBySociete() {
        // Créer des documents pour différents exercices
        Document doc2024 = createTestDocument("DOC-2024-001", 2024);
        doc2024.setDatePiece(LocalDate.of(2024, 3, 15));

        Document doc2023 = createTestDocument("DOC-2023-001", 2023);
        doc2023.setDatePiece(LocalDate.of(2023, 5, 20));

        entityManager.persist(doc2024);
        entityManager.persist(doc2023);
        entityManager.flush();

        // Récupérer tous les documents et vérifier qu'ils appartiennent à la bonne société
        List<Document> allDocuments = documentRepository.findAll();

        // Vérifications
        assertThat(allDocuments).isNotEmpty();
        assertThat(allDocuments).allMatch(doc -> doc.getSociete().getId().equals(societe.getId()));
    }

    @Test
    void testDeleteDocument() {
        // Créer et sauvegarder un document
        Document document = createTestDocument("DOC-005", 2024);
        document = entityManager.persistAndFlush(document);
        Long documentId = document.getId();

        // Supprimer le document
        documentRepository.deleteById(documentId);
        entityManager.flush();

        // Vérifier que le document n'existe plus
        Optional<Document> deletedDocument = documentRepository.findById(documentId);
        assertThat(deletedDocument).isEmpty();
    }

    @Test
    void testUpdateDocument() {
        // Créer et sauvegarder un document
        Document document = createTestDocument("DOC-006", 2024);
        document = entityManager.persistAndFlush(document);

        // Modifier le document
        document.setStatut(Document.StatutDocument.VALIDE);
        document.setCommentaireComptable("Document validé");
        Document updatedDocument = documentRepository.save(document);
        entityManager.flush();

        // Vérifications
        assertThat(updatedDocument.getStatut()).isEqualTo(Document.StatutDocument.VALIDE);
        assertThat(updatedDocument.getCommentaireComptable()).isEqualTo("Document validé");
    }

    @Test
    void testCountDocuments() {
        // Créer plusieurs documents
        Document doc1 = createTestDocument("DOC-007", 2024);
        Document doc2 = createTestDocument("DOC-008", 2024);
        entityManager.persist(doc1);
        entityManager.persist(doc2);
        entityManager.flush();

        // Compter les documents
        long count = documentRepository.count();

        // Vérifications
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    // Méthode utilitaire pour créer un document de test
    private Document createTestDocument(String numeroPiece, int exercice) {
        return Document.builder()
                .numeroPiece(numeroPiece)
                .type(Document.TypeDocument.FACTURE_ACHAT)
                .datePiece(LocalDate.of(exercice, 1, 15))
                .montant(new BigDecimal("1000.00"))
                .fournisseur("Fournisseur Test")
                .cheminFichier("/uploads/" + numeroPiece + ".pdf")
                .nomFichierOriginal(numeroPiece + ".pdf")
                .typeFichier("application/pdf")
                .tailleFichier(10000L)
                .statut(Document.StatutDocument.EN_ATTENTE)
                .societe(societe)
                .uploadePar(utilisateur)
                .exerciceComptable(String.valueOf(exercice))
                .dateCreation(LocalDateTime.now())
                .build();
    }
}

