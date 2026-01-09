package com.example.smartplanner.config;

import com.example.smartplanner.entity.Role;
import com.example.smartplanner.entity.User;
import com.example.smartplanner.repository.RoleRepository;
import com.example.smartplanner.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(RoleRepository roles, UserRepository users, PasswordEncoder encoder) {
        return args -> {
            Role admin = roles.findByName("ADMIN").orElseGet(() -> roles.save(Role.builder().name("ADMIN").build()));
            Role manager = roles.findByName("MANAGER").orElseGet(() -> roles.save(Role.builder().name("MANAGER").build()));
            Role userRole = roles.findByName("USER").orElseGet(() -> roles.save(Role.builder().name("USER").build()));

            // Admin account
            if (!users.existsByEmail("admin@smartplanner.local")) {
                users.save(User.builder()
                        .email("admin@smartplanner.local")
                        .passwordHash(encoder.encode("AdminPass123!"))
                        .name("Admin")
                        .surname("Root")
                        .role(admin)
                        .profileImageUrl("https://example.com/admin.png")
                        .build());
            }

            // Manager account
            if (!users.existsByEmail("manager@smartplanner.local")) {
                users.save(User.builder()
                        .email("manager@smartplanner.local")
                        .passwordHash(encoder.encode("ManagerPass123!"))
                        .name("Manager")
                        .surname("Lead")
                        .role(manager)
                        .profileImageUrl("https://example.com/manager.png")
                        .build());
            }

            // 10 random users
            List<String> firstNames = List.of("Luca","Marco","Giulia","Sara","Davide","Elena","Matteo","Chiara","Andrea","Francesca",
                    "Paolo","Marta","Simone","Ilaria","Giorgio","Valentina");
            List<String> lastNames = List.of("Rossi","Bianchi","Ferrari","Romano","Gallo","Costa","Fontana","Esposito","Ricci","Marino",
                    "Greco","Conti","Mancini","Lombardi","Moretti","Barbieri");

            Random rnd = new Random();
            int created = 0;

            while (created < 10) {
                String fn = firstNames.get(rnd.nextInt(firstNames.size()));
                String ln = lastNames.get(rnd.nextInt(lastNames.size()));
                String email = (fn + "." + ln + (100 + rnd.nextInt(900)) + "@example.com").toLowerCase();

                if (users.existsByEmail(email)) continue;

                // Mostly USER, sometimes MANAGER (no random ADMIN)
                Role role = (rnd.nextInt(10) == 0) ? manager : userRole;

                users.save(User.builder()
                        .email(email)
                        .passwordHash(encoder.encode("UserPass123!"))
                        .name(fn)
                        .surname(ln)
                        .role(role)
                        .profileImageUrl("https://example.com/avatar/" + fn.toLowerCase() + ".png")
                        .build());

                created++;
            }
        };
    }
}
