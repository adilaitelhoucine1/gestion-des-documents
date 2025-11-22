package org.example.gestiondesdocuments.repository;

import org.example.gestiondesdocuments.entite.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveRole() {
        // Créer un rôle
        Role role = Role.builder()
                .nom(Role.TypeRole.ROLE_COMPTABLE)
                .description("Rôle comptable pour gérer les documents")
                .dateCreation(LocalDateTime.now())
                .build();

        // Sauvegarder le rôle
        Role savedRole = roleRepository.save(role);

        // Vérifications
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getNom()).isEqualTo(Role.TypeRole.ROLE_COMPTABLE);
        assertThat(savedRole.getDescription()).isEqualTo("Rôle comptable pour gérer les documents");
    }

    @Test
    void testFindById() {
        // Créer et sauvegarder un rôle
        Role role = createTestRole(Role.TypeRole.ROLE_SOCIETE, "Rôle société");
        role = entityManager.persistAndFlush(role);

        // Rechercher le rôle par ID
        Optional<Role> foundRole = roleRepository.findById(role.getId());

        // Vérifications
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getNom()).isEqualTo(Role.TypeRole.ROLE_SOCIETE);
    }

    @Test
    void testFindAll() {
        // Créer plusieurs rôles
        Role role1 = createTestRole(Role.TypeRole.ROLE_COMPTABLE, "Comptable");
        Role role2 = createTestRole(Role.TypeRole.ROLE_SOCIETE, "Société");
        entityManager.persist(role1);
        entityManager.persist(role2);
        entityManager.flush();

        // Récupérer tous les rôles
        List<Role> roles = roleRepository.findAll();

        // Vérifications
        assertThat(roles).isNotEmpty();
        assertThat(roles.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateRole() {
        // Créer et sauvegarder un rôle
        Role role = createTestRole(Role.TypeRole.ROLE_COMPTABLE, "Description initiale");
        role = entityManager.persistAndFlush(role);

        // Modifier le rôle
        role.setDescription("Description mise à jour");
        Role updatedRole = roleRepository.save(role);
        entityManager.flush();

        // Vérifications
        assertThat(updatedRole.getDescription()).isEqualTo("Description mise à jour");
    }

    @Test
    void testDeleteRole() {
        // Créer et sauvegarder un rôle
        Role role = createTestRole(Role.TypeRole.ROLE_COMPTABLE, "Rôle à supprimer");
        role = entityManager.persistAndFlush(role);
        Long roleId = role.getId();

        // Supprimer le rôle
        roleRepository.deleteById(roleId);
        entityManager.flush();

        // Vérifier que le rôle n'existe plus
        Optional<Role> deletedRole = roleRepository.findById(roleId);
        assertThat(deletedRole).isEmpty();
    }

    @Test
    void testCountRoles() {
        // Créer plusieurs rôles
        Role role1 = createTestRole(Role.TypeRole.ROLE_COMPTABLE, "Role 1");
        Role role2 = createTestRole(Role.TypeRole.ROLE_SOCIETE, "Role 2");
        entityManager.persist(role1);
        entityManager.persist(role2);
        entityManager.flush();

        // Compter les rôles
        long count = roleRepository.count();

        // Vérifications
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testSaveRoleComptable() {
        // Créer un rôle comptable
        Role roleComptable = Role.builder()
                .nom(Role.TypeRole.ROLE_COMPTABLE)
                .description("Rôle pour les comptables")
                .dateCreation(LocalDateTime.now())
                .build();

        // Sauvegarder
        Role savedRole = roleRepository.save(roleComptable);

        // Vérifications
        assertThat(savedRole.getNom()).isEqualTo(Role.TypeRole.ROLE_COMPTABLE);
    }

    @Test
    void testSaveRoleSociete() {
        // Créer un rôle société
        Role roleSociete = Role.builder()
                .nom(Role.TypeRole.ROLE_SOCIETE)
                .description("Rôle pour les sociétés")
                .dateCreation(LocalDateTime.now())
                .build();

        // Sauvegarder
        Role savedRole = roleRepository.save(roleSociete);

        // Vérifications
        assertThat(savedRole.getNom()).isEqualTo(Role.TypeRole.ROLE_SOCIETE);
    }

    @Test
    void testExistsById() {
        // Créer et sauvegarder un rôle
        Role role = createTestRole(Role.TypeRole.ROLE_COMPTABLE, "Test existence");
        role = entityManager.persistAndFlush(role);

        // Vérifier l'existence
        boolean exists = roleRepository.existsById(role.getId());

        // Vérifications
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByIdNotFound() {
        // Vérifier l'existence d'un ID inexistant
        boolean exists = roleRepository.existsById(999999L);

        // Vérifications
        assertThat(exists).isFalse();
    }

    // Méthode utilitaire pour créer un rôle de test
    private Role createTestRole(Role.TypeRole typeRole, String description) {
        return Role.builder()
                .nom(typeRole)
                .description(description)
                .dateCreation(LocalDateTime.now())
                .build();
    }
}

