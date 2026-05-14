package com.king.lms.e_learning_hub.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.king.lms.e_learning_hub.entity.Role;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.repository.RoleRepository;
import com.king.lms.e_learning_hub.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder encoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {


        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role r = roleRepository.save(Role.builder()
                        .name("admin")
                        .description("admin role")
                        .build());

                Role r1 = roleRepository.save(Role.builder()
                        .name("customer")
                        .description("customer role")
                        .build());

                HashSet<Role> roles = new HashSet<>();
                roles.add(r);
                roles.add(r1);

                User u = User.builder()
                        .fullName("admin")
                        .password(encoder.encode("admin"))
                        .username("admin")
                        .email(" ")
                        .roles(roles)
                        .build();

                userRepository.save(u);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }

}
