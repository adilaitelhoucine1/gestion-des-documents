package org.example.gestiondesdocuments.config;

import org.example.gestiondesdocuments.entite.Role;
import org.example.gestiondesdocuments.entite.Societe;
import org.example.gestiondesdocuments.entite.Utilisateur;
import org.example.gestiondesdocuments.repository.RoleRepository;
import org.example.gestiondesdocuments.repository.SocietyRepository;
import org.example.gestiondesdocuments.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SocietyRepository societyRepository;
    private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, SocietyRepository societyRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.societyRepository = societyRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (roleRepository.count() > 0) {
            System.out.println("Data already initialized, skipping...");
            return;
        }

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


        Utilisateur utilisateur1 = Utilisateur.builder()
                .email("user1@example.com")
                .motDePasse(passwordEncoder.encode("password123"))
                .nomComplet("Ahmed El Houcine")
                .societe(societe)
                .actif(true)
                .roles(Set.of(roleSociete))
                .build();

        Utilisateur utilisateur2 = Utilisateur.builder()
                .email("comptable1@example.com")
                .motDePasse(passwordEncoder.encode("secret456"))
                .nomComplet("Fatima Zahra")
                .societe(null)
                .actif(true)
                .roles(Set.of(roleComptable))
                .build();

        userRepository.saveAll(List.of(utilisateur1, utilisateur2));
        System.out.println("Initial data created successfully!");
    }
}
