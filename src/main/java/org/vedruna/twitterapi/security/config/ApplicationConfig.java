package org.vedruna.twitterapi.security.config;

import java.util.NoSuchElementException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vedruna.twitterapi.persistance.repository.UserRepository;

import lombok.AllArgsConstructor;

/**
 * Beans relacionados con autenticación (UserDetailsService, PasswordEncoder, AuthenticationProvider...).
 */
@Configuration
@AllArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepo;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailService() {
        return username -> {
            // En tu repo findByUsername devuelve UserEntity (no Optional) según tu repo actual.
            var u = userRepo.findByUsername(username);
            if (u == null) {
                throw new NoSuchElementException("User not found");
            }
            return u; // UserEntity implements UserDetails
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
