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
 * Configuración de beans de autenticación para la aplicación.
 *
 * <p>Proporciona beans para:</p>
 * <ul>
 *   <li>{@link UserDetailsService}: carga de usuarios desde la base de datos</li>
 *   <li>{@link PasswordEncoder}: codificación de contraseñas (BCrypt)</li>
 *   <li>{@link AuthenticationProvider}: proveedor de autenticación que combina {@link UserDetailsService} y {@link PasswordEncoder}</li>
 *   <li>{@link AuthenticationManager}: manager de autenticación para procesar credenciales</li>
 * </ul>
 *
 * <p>Todos los beans están configurados para integrarse con Spring Security y tu repositorio actual
 * {@link UserRepository}.</p>
 */
@Configuration
@AllArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepo;

    /**
     * Bean de {@link AuthenticationManager}.
     *
     * <p>Se obtiene a partir de {@link AuthenticationConfiguration} y se utiliza para
     * autenticar usuarios mediante username/password en {@link AuthService}.</p>
     *
     * @param config configuración de autenticación
     * @return {@link AuthenticationManager} listo para autenticar credenciales
     * @throws Exception si ocurre un error al construir el manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean de {@link AuthenticationProvider}.
     *
     * <p>Proveedor de autenticación basado en DAO que combina {@link UserDetailsService} y
     * {@link PasswordEncoder} para validar credenciales contra la base de datos.</p>
     *
     * @return {@link AuthenticationProvider} configurado con DAO
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Bean de {@link UserDetailsService}.
     *
     * <p>Se encarga de cargar un usuario desde la base de datos usando el username. Devuelve
     * un {@link org.springframework.security.core.userdetails.UserDetails} (tu {@link org.vedruna.twitterapi.persistance.entity.UserEntity})
     * que implementa esta interfaz.</p>
     *
     * <p>Lanza {@link NoSuchElementException} si no encuentra el usuario.</p>
     *
     * @return {@link UserDetailsService} listo para integrarse con Spring Security
     */
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

    
    /**
     * Bean de {@link PasswordEncoder}.
     *
     * <p>Usa {@link BCryptPasswordEncoder} para codificar contraseñas de manera segura.</p>
     *
     * @return {@link PasswordEncoder} listo para codificar y verificar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
