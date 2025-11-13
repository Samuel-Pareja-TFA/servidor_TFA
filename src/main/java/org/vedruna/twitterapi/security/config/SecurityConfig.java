package org.vedruna.twitterapi.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.vedruna.twitterapi.security.filter.JwtAuthenticationFilter;

/**
 * SecurityConfig consolidado: permite explícitamente los endpoints públicos que
 * indicaste y protege el resto.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Autorizar rutas
            .authorizeHttpRequests(auth -> auth
                // permitir preflight OPTIONS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Endpoints públicos concretos (según tus requisitos)
                .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()   // 1) register
                .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()      // 2) login

                // 3) editar username -> privado (PATCH) -> NO permitAll (queda autenticado)
                // 4) buscar usuario por username -> público (GET)
                .requestMatchers(HttpMethod.GET, "/api/v1/users/by-username/**").permitAll()

                // Publicaciones de un usuario (GET) -> público según tus indicaciones
                .requestMatchers(HttpMethod.GET, "/api/v1/publications/user/**").permitAll()

                // Swagger / OpenAPI docs (si los usas)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Si usas un controlador /auth/** (opcional) lo permitimos también
                .requestMatchers("/api/v1/auth/**", "/auth/**").permitAll()

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            // Stateless session -> JWT
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Provider + filtro JWT
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
