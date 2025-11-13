package org.vedruna.twitterapi.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.controller.dto.LoginDto;
import org.vedruna.twitterapi.controller.dto.TokenDto;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.AuthService;
import org.vedruna.twitterapi.service.exception.AuthenticationFailedException;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación simple y temporal del servicio de autenticación.
 *
 * <p>Esta implementación está pensada únicamente para pruebas y demostraciones. Su flujo es el
 * siguiente:
 * <ol>
 *   <li>Busca el usuario por {@code username} usando {@link UserRepository}.</li>
 *   <li>Compara la contraseña en texto plano (NO SE DEBE USAR EN PRODUCCIÓN).</li>
 *   <li>Genera y devuelve un token ficticio (UUID) empaquetado en {@link TokenDto}.</li>
 * </ol>
 *
 * <p>Importante: en una aplicación productiva este servicio debería:
 * <ul>
 *   <li>Usar BCrypt (o scrypt/argon2) para almacenar/validar contraseñas.</li>
 *   <li>Emitir JWT firmado (o tokens opacos gestionados por un proveedor), no UUIDs aleatorios.</li>
 *   <li>Integrarse con Spring Security para gestión de contexto de seguridad y filtros.</li>
 * </ul>
 */
@Slf4j
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    /** Repositorio usado para buscar usuarios por username. */
    private final UserRepository userRepository;

    /**
     * Intenta autenticar un usuario con las credenciales proporcionadas en {@link LoginDto}.
     *
     * <p>Comportamiento:
     * <ol>
     *   <li>Busca el usuario por username; si no existe lanza {@link UserNotFoundException}.</li>
     *   <li>Compara la contraseña en claro (temporal); si no coincide lanza
     *       {@link AuthenticationFailedException}.</li>
     *   <li>Genera un token aleatorio (UUID) y lo devuelve en un {@link TokenDto}.
     *       Este token no provee ninguna firma ni expiración y sólo sirve para pruebas.</li>
     * </ol>
     *
     * @param loginDto DTO con username y password en texto plano.
     * @return {@link TokenDto} que contiene el token generado para la sesión (ficticio).
     * @throws UserNotFoundException si no existe un usuario con el username proporcionado.
     * @throws AuthenticationFailedException si la contraseña no coincide.
     */
    @Override
    @Transactional(readOnly = true)
    public TokenDto login(LoginDto loginDto) {
        log.info("AuthService: intento de login para username {}", loginDto.getUsername());
        UserEntity u = userRepository.findByUsername(loginDto.getUsername());
        if (u == null) {
            // excepción personalizada (usuario no encontrado)
            throw new UserNotFoundException("User not found with username " + loginDto.getUsername());
        }

        // COMPARACIÓN EN CLARO (temporal). Reemplazar por BCrypt.compare en producción
        if (!u.getPassword().equals(loginDto.getPassword())) {
            log.warn("AuthService: credenciales inválidas para username {}", loginDto.getUsername());
            throw new AuthenticationFailedException("Username or password is incorrect");
        }

        // Generamos token ficticio (UUID) para pruebas
        String token = UUID.randomUUID().toString();
        log.info("AuthService: login correcto para {} -> token {}", loginDto.getUsername(), token);
        return new TokenDto(token);
    }
}
