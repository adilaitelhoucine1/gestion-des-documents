package org.example.gestiondesdocuments.config;

import org.example.gestiondesdocuments.entite.Role;
import org.example.gestiondesdocuments.entite.Societe;
import org.example.gestiondesdocuments.entite.Utilisateur;
import org.example.gestiondesdocuments.repository.RoleRepository;
import org.example.gestiondesdocuments.repository.SocietyRepository;
import org.example.gestiondesdocuments.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SocietyRepository societyRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(UserRepository userRepository, SocietyRepository societyRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.societyRepository = societyRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Save Roles first
        Role roleComptable = Role.builder()
                .nom(Role.TypeRole.ROLE_COMPTABLE)
                .build();
        roleComptable = roleRepository.save(roleComptable);

        Role roleSociete = Role.builder()
                .nom(Role.TypeRole.ROLE_SOCIETE)
                .build();
        roleSociete = roleRepository.save(roleSociete);

        // Save Societe
        Societe societe = Societe.builder()
                .raisonSociale("Al Amane")
                .ice("ICE123456")
                .adresse("Casablanca, Maroc")
                .emailContact("contact@al-amane.ma")
                .telephone("0522123456")
                .build();
        societe = societyRepository.save(societe);

        // Create and save users with saved entities
        Utilisateur utilisateur1 = Utilisateur.builder()
                .email("user1@example.com")
                .motDePasse("password123") // à encoder avec BCrypt pour production
                .nomComplet("Ahmed El Houcine")
                .societe(societe)
                .actif(true)
                .roles(Set.of(roleSociete))
                .build();

        Utilisateur utilisateur2 = Utilisateur.builder()
                .email("comptable1@example.com")
                .motDePasse("secret456")
                .nomComplet("Fatima Zahra")
                .societe(null) // Comptable peut ne pas avoir de société rattachée
                .actif(true)
                .roles(Set.of(roleComptable))
                .build();

        userRepository.saveAll(List.of(utilisateur1, utilisateur2));
    }
}
