package com.authservice.iam.config;

import com.authservice.iam.entity.Role;
import com.authservice.iam.entity.User;
import com.authservice.iam.repository.RoleRepository;
import com.authservice.iam.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner seedData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      BootstrapAdminProperties adminProperties) {
        return args -> {
            Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
                Role role = new Role();
                role.setName("USER");
                role.setDescription("Default user role");
                return roleRepository.save(role);
            });

            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ADMIN");
                role.setDescription("Administrator role");
                return roleRepository.save(role);
            });

            if (adminProperties.isConfigured()) {
                String email = adminProperties.getEmail().trim().toLowerCase(Locale.ROOT);
                User admin = userRepository.findByEmail(email).orElse(null);
                if (admin == null) {
                    admin = new User();
                    admin.setEmail(email);
                    admin.setUsername(deriveUsername(email));
                    admin.setPasswordHash(passwordEncoder.encode(adminProperties.getPassword()));
                }
                if (admin.getUsername() == null || admin.getUsername().isBlank()) {
                    admin.setUsername(deriveUsername(email));
                }
                admin.getRoles().add(adminRole);
                userRepository.save(admin);
            }
        };
    }

    private String deriveUsername(String email) {
        if (email == null) {
            return null;
        }
        String localPart = email.split("@", 2)[0];
        return localPart.trim().toLowerCase(Locale.ROOT);
    }
}
