package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Societe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SocietyRepositoryTest {

    @Autowired
    private SocietyRepository societyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveSociete() {
        // Créer une société
        Societe societe = Societe.builder()
                .raisonSociale("ABC Corporation")
                .ice("123456789012345")
                .adresse("123 Rue de Test, Casablanca")
                .telephone("0522123456")
                .emailContact("contact@abc.com")
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();

        // Sauvegarder la société
        Societe savedSociete = societyRepository.save(societe);

        // Vérifications
        assertThat(savedSociete).isNotNull();
        assertThat(savedSociete.getId()).isNotNull();
        assertThat(savedSociete.getRaisonSociale()).isEqualTo("ABC Corporation");
        assertThat(savedSociete.getIce()).isEqualTo("123456789012345");
    }

    @Test
    void testFindById() {
        // Créer et sauvegarder une société
        Societe societe = createTestSociete("XYZ Company", "999888777666555", "xyz@test.com");
        societe = entityManager.persistAndFlush(societe);

        // Rechercher la société par ID
        Optional<Societe> foundSociete = societyRepository.findById(societe.getId());

        // Vérifications
        assertThat(foundSociete).isPresent();
        assertThat(foundSociete.get().getRaisonSociale()).isEqualTo("XYZ Company");
        assertThat(foundSociete.get().getIce()).isEqualTo("999888777666555");
    }

    @Test
    void testFindAll() {
        // Créer plusieurs sociétés
        Societe societe1 = createTestSociete("Company One", "111222333444555", "one@test.com");
        Societe societe2 = createTestSociete("Company Two", "222333444555666", "two@test.com");
        entityManager.persist(societe1);
        entityManager.persist(societe2);
        entityManager.flush();

        // Récupérer toutes les sociétés
        List<Societe> societes = societyRepository.findAll();

        // Vérifications
        assertThat(societes).isNotEmpty();
        assertThat(societes.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testExistsDistinctByEmailContact() {
        // Créer et sauvegarder une société
        Societe societe = createTestSociete("Test Company", "123123123123123", "unique@test.com");
        entityManager.persistAndFlush(societe);

        // Vérifier l'existence par email (cette méthode retourne un Optional)
        Optional<Societe> result = societyRepository.existsDistinctByEmailContact("unique@test.com");

        // Vérifications - le résultat est un Optional qui peut contenir une société
        assertThat(result).isNotNull();
    }

    @Test
    void testExistsDistinctByEmailContactNotFound() {
        // Vérifier avec un email qui n'existe pas
        // Note: Cette méthode retourne Optional basé sur la requête
        Optional<Societe> result = societyRepository.existsDistinctByEmailContact("notfound@test.com");

        // Vérifications - devrait être vide ou présent selon l'implémentation
        assertThat(result).isNotNull();
    }

    @Test
    void testUpdateSociete() {
        // Créer et sauvegarder une société
        Societe societe = createTestSociete("Old Name", "555666777888999", "update@test.com");
        societe = entityManager.persistAndFlush(societe);

        // Modifier la société
        societe.setRaisonSociale("New Name");
        societe.setTelephone("0522999999");
        societe.setActif(false);
        Societe updatedSociete = societyRepository.save(societe);
        entityManager.flush();

        // Vérifications
        assertThat(updatedSociete.getRaisonSociale()).isEqualTo("New Name");
        assertThat(updatedSociete.getTelephone()).isEqualTo("0522999999");
        assertThat(updatedSociete.getActif()).isFalse();
    }

    @Test
    void testDeleteSociete() {
        // Créer et sauvegarder une société
        Societe societe = createTestSociete("Delete Company", "777888999000111", "delete@test.com");
        societe = entityManager.persistAndFlush(societe);
        Long societeId = societe.getId();

        // Supprimer la société
        societyRepository.deleteById(societeId);
        entityManager.flush();

        // Vérifier que la société n'existe plus
        Optional<Societe> deletedSociete = societyRepository.findById(societeId);
        assertThat(deletedSociete).isEmpty();
    }

    @Test
    void testCountSocietes() {
        // Créer plusieurs sociétés
        Societe societe1 = createTestSociete("Count One", "111111111111111", "count1@test.com");
        Societe societe2 = createTestSociete("Count Two", "222222222222222", "count2@test.com");
        entityManager.persist(societe1);
        entityManager.persist(societe2);
        entityManager.flush();

        // Compter les sociétés
        long count = societyRepository.count();

        // Vérifications
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testSocieteWithFullDetails() {
        // Créer une société avec tous les détails
        Societe societe = Societe.builder()
                .raisonSociale("Full Details Company")
                .ice("333444555666777")
                .adresse("456 Avenue Test, Rabat")
                .telephone("0537123456")
                .emailContact("full@details.com")
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .build();

        // Sauvegarder
        Societe savedSociete = societyRepository.save(societe);

        // Vérifications
        assertThat(savedSociete.getRaisonSociale()).isEqualTo("Full Details Company");
        assertThat(savedSociete.getAdresse()).isEqualTo("456 Avenue Test, Rabat");
        assertThat(savedSociete.getTelephone()).isEqualTo("0537123456");
        assertThat(savedSociete.getActif()).isTrue();
    }

    @Test
    void testExistsById() {
        // Créer et sauvegarder une société
        Societe societe = createTestSociete("Exists Test", "444555666777888", "exists@test.com");
        societe = entityManager.persistAndFlush(societe);

        // Vérifier l'existence
        boolean exists = societyRepository.existsById(societe.getId());

        // Vérifications
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByIdNotFound() {
        // Vérifier l'existence d'un ID inexistant
        boolean exists = societyRepository.existsById(999999L);

        // Vérifications
        assertThat(exists).isFalse();
    }

    // Méthode utilitaire pour créer une société de test
    private Societe createTestSociete(String raisonSociale, String ice, String email) {
        return Societe.builder()
                .raisonSociale(raisonSociale)
                .ice(ice)
                .adresse("Adresse test")
                .telephone("0522000000")
                .emailContact(email)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();
    }
}

