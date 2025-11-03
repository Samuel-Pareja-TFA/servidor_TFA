package org.vedruna.twitterapi.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de UserService.
 *
 * @Slf4j proporciona el logger {@code log}.
 * @AllArgsConstructor genera constructor para inyección de dependencias.
 * @Service marca la clase como componente de Spring.
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserEntity createUser(UserEntity user) {
        log.info("Creando usuario: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity saved = userRepository.save(user);
        log.info("Usuario creado con id {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserById(Integer userId) {
        log.info("Buscando usuario por id {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByUsername(String username) {
        log.info("Buscando usuario por username {}", username);
        UserEntity u = userRepository.findByUsername(username);
        if (u == null) throw new EntityNotFoundException("User not found with username " + username);
        return u;
    }

    @Override
    @Transactional
    public UserEntity updateUsername(Integer userId, String newUsername) {
        log.info("Actualizando username para userId {} -> {}", userId, newUsername);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setUsername(newUsername);
        UserEntity updated = userRepository.save(user);
        log.info("Username actualizado para userId {} a {}", userId, updated.getUsername());
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> getFollowing(Integer userId, Pageable pageable) {
        log.info("Obteniendo following de userId {}", userId);
        // valida existencia del usuario
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id " + userId);
        }
        return userRepository.findFollowingByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> getFollowers(Integer userId, Pageable pageable) {
        log.info("Obteniendo followers de userId {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id " + userId);
        }
        return userRepository.findFollowersByUserId(userId, pageable);
    }
}
