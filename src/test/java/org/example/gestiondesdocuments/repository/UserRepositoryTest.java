package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Societe;
import org.example.gestiondesdocuments.entite.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Societe societe;

    @BeforeEach
    void setUp() {
        // Créer une société de test
        societe = Societe.builder()
                .raisonSociale("Test Company")
                .ice("123456789012345")
                .emailContact("company@test.com")
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();
        societe = entityManager.persistAndFlush(societe);
    }

    @Test
    void testSaveUtilisateur() {
        // Créer un utilisateur
        Utilisateur utilisateur = Utilisateur.builder()
                .email("john.doe@test.com")
                .motDePasse("password123")
                .nomComplet("John Doe")
                .societe(societe)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();

        // Sauvegarder l'utilisateur
        Utilisateur savedUser = userRepository.save(utilisateur);

        // Vérifications
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@test.com");
        assertThat(savedUser.getNomComplet()).isEqualTo("John Doe");
    }

    @Test
    void testFindByEmail() {
        // Créer et sauvegarder un utilisateur
        Utilisateur utilisateur = createTestUser("jane.doe@test.com", "Jane Doe");
        entityManager.persistAndFlush(utilisateur);

        // Rechercher par email
        Optional<Utilisateur> foundUser = userRepository.findByEmail("jane.doe@test.com");

        // Vérifications
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane.doe@test.com");
        assertThat(foundUser.get().getNomComplet()).isEqualTo("Jane Doe");
    }

    @Test
    void testFindByEmailNotFound() {
        // Rechercher un email qui n'existe pas
        Optional<Utilisateur> foundUser = userRepository.findByEmail("nonexistent@test.com");

        // Vérifications
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testFindById() {
        // Créer et sauvegarder un utilisateur
        Utilisateur utilisateur = createTestUser("bob.smith@test.com", "Bob Smith");
        utilisateur = entityManager.persistAndFlush(utilisateur);

        // Rechercher par ID
        Optional<Utilisateur> foundUser = userRepository.findById(utilisateur.getId());

        // Vérifications
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("bob.smith@test.com");
    }

    @Test
    void testFindAll() {
        // Créer plusieurs utilisateurs
        Utilisateur user1 = createTestUser("user1@test.com", "User One");
        Utilisateur user2 = createTestUser("user2@test.com", "User Two");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // Récupérer tous les utilisateurs
        List<Utilisateur> users = userRepository.findAll();

        // Vérifications
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateUtilisateur() {
        // Créer et sauvegarder un utilisateur
        Utilisateur utilisateur = createTestUser("update@test.com", "Update User");
        utilisateur = entityManager.persistAndFlush(utilisateur);

        // Modifier l'utilisateur
        utilisateur.setNomComplet("Updated Name");
        utilisateur.setActif(false);
        Utilisateur updatedUser = userRepository.save(utilisateur);
        entityManager.flush();

        // Vérifications
        assertThat(updatedUser.getNomComplet()).isEqualTo("Updated Name");
        assertThat(updatedUser.getActif()).isFalse();
    }

    @Test
    void testDeleteUtilisateur() {
        // Créer et sauvegarder un utilisateur
        Utilisateur utilisateur = createTestUser("delete@test.com", "Delete User");
        utilisateur = entityManager.persistAndFlush(utilisateur);
        Long userId = utilisateur.getId();

        // Supprimer l'utilisateur
        userRepository.deleteById(userId);
        entityManager.flush();

        // Vérifier que l'utilisateur n'existe plus
        Optional<Utilisateur> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testCountUtilisateurs() {
        // Créer plusieurs utilisateurs
        Utilisateur user1 = createTestUser("count1@test.com", "Count One");
        Utilisateur user2 = createTestUser("count2@test.com", "Count Two");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // Compter les utilisateurs
        long count = userRepository.count();

        // Vérifications
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUtilisateurWithSociete() {
        // Créer un utilisateur avec une société
        Utilisateur utilisateur = createTestUser("society@test.com", "Society User");
        utilisateur = entityManager.persistAndFlush(utilisateur);

        // Récupérer l'utilisateur
        Optional<Utilisateur> foundUser = userRepository.findById(utilisateur.getId());

        // Vérifications
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getSociete()).isNotNull();
        assertThat(foundUser.get().getSociete().getRaisonSociale()).isEqualTo("Test Company");
    }

    // Méthode utilitaire pour créer un utilisateur de test
    private Utilisateur createTestUser(String email, String nomComplet) {
        return Utilisateur.builder()
                .email(email)
                .motDePasse("password")
                .nomComplet(nomComplet)
                .societe(societe)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();
    }
}

