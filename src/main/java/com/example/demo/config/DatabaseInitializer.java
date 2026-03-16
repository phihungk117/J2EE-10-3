package com.example.demo.config;

import com.example.demo.entity.Account;
import com.example.demo.entity.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initDatabase(RoleRepository roleRepository,
                                          AccountRepository accountRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if ROLE_ADMIN exists, if not, create
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }

            // Check if ROLE_USER exists, if not, create
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole == null) {
                userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }

            // Update or create admin user
            Optional<Account> adminOpt = accountRepository.findByLoginName("admin");
            Account admin = adminOpt.isPresent() ? adminOpt.get() : new Account();
            admin.setLogin_name("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.getRoles().add(adminRole);
            accountRepository.save(admin);

            // Update or create normal user
            Optional<Account> userOpt = accountRepository.findByLoginName("user");
            Account user = userOpt.isPresent() ? userOpt.get() : new Account();
            user.setLogin_name("user");
            user.setPassword(passwordEncoder.encode("123456"));
            user.getRoles().add(userRole);
            accountRepository.save(user);
        };
    }
}
