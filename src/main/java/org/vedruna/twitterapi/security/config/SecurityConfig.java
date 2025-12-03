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

/* IMPORTS AÑADIDOS PARA CORS */
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración central de seguridad de Spring Security.
 *
 * <p>Proporciona:</p>
 * <ul>
 *   <li>Permisos explícitos para endpoints públicos (register, login, búsqueda de usuario, publicaciones públicas, Swagger/OpenAPI).</li>
 *   <li>Protección de endpoints privados mediante autenticación JWT.</li>
 *   <li>Configuración stateless de la sesión (sin cookies, solo JWT).</li>
 *   <li>Integración del filtro {@link JwtAuthenticationFilter} antes de
 *       {@link UsernamePasswordAuthenticationFilter} para validar tokens.</li>
 * </ul>
 *
 * <p>Se habilita:</p>
 * <ul>
 *   <li>{@link EnableWebSecurity} para seguridad web básica</li>
 *   <li>{@link EnableMethodSecurity} para anotaciones de seguridad a nivel de métodos</li>
 * </ul>
 *
 * <p>El bean {@link SecurityFilterChain} define la política de autorización, los endpoints permitidos y
 * la gestión de sesiones.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    /**
     * Define la cadena de filtros de seguridad de Spring Security.
     *
     * <p>Configura:</p>
     * <ul>
     *   <li>Deshabilita CSRF (aplicable en APIs REST con JWT)</li>
     *   <li>Permite preflight OPTIONS para CORS</li>
     *   <li>Autoriza explícitamente ciertos endpoints públicos</li>
     *   <li>Requiere autenticación para cualquier otro endpoint</li>
     *   <li>Define sesión stateless para usar solo JWT</li>
     *   <li>Registra el {@link AuthenticationProvider} y el filtro {@link JwtAuthenticationFilter}</li>
     * </ul>
     *
     * @param http objeto HttpSecurity para configurar la seguridad HTTP
     * @return {@link SecurityFilterChain} configurado
     * @throws Exception si ocurre un error al construir la cadena de seguridad
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
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
                
                // Comentarios de una publicación (GET) -> público según tus indicaciones
                .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
                
                // Likes de una publicación (GET) -> público según tus indicaciones
                .requestMatchers(HttpMethod.GET, "/api/v1/likes/**").permitAll()

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

    /**
     * Configuración global de CORS para toda la API.
     *
     * <p>Permite que el frontend en http://localhost:5173
     * pueda acceder a los endpoints de este backend con los métodos y cabeceras indicados.</p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origen del front
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // Métodos permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Cabeceras permitidas
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Si en algún momento usas cookies/sesiones, esto ayuda.
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración CORS a todos los endpoints
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
