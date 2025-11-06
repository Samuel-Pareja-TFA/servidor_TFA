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
 * Implementación temporal de autenticación:
 * - Busca usuario por username
 * - Compara la contraseña en claro (solo para pruebas)
 * - Devuelve un token ficticio (UUID)
 *
 * NOTA: Reemplazar por Spring Security + JWT + BCrypt para la versión final.
 */
@Slf4j
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

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
