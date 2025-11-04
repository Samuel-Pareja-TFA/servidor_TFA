package org.vedruna.twitterapi.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.RoleService;
import org.vedruna.twitterapi.service.UserService;
import org.vedruna.twitterapi.service.exception.EmailConflictException;
import org.vedruna.twitterapi.service.exception.RoleNotFoundException;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;
import org.vedruna.twitterapi.service.exception.UsernameConflictException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de UserService.
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    @Transactional
    public UserEntity createUser(UserEntity user) {
        log.info("Creando usuario: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Username conflict al crear usuario: {}", user.getUsername());
            throw new UsernameConflictException();
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Email conflict al crear usuario: {}", user.getEmail());
            throw new EmailConflictException();
        }

        // --- FIX: asignar createDate si no viene en el DTO/Entity ---
        if (user.getCreateDate() == null) {
            user.setCreateDate(LocalDate.now());
        }

        // --- Asignar rol por defecto si no se ha indicado ---
        if (user.getRole() == null) {
            roleService.findByName("Usuario")
                .ifPresentOrElse(
                    user::setRole,
                    () -> {
                        log.error("Role por defecto 'Usuario' no encontrado al crear usuario {}", user.getUsername());
                        throw new RoleNotFoundException("Default role 'Usuario' not found");
                    }
                );
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
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByUsername(String username) {
        log.info("Buscando usuario por username {}", username);
        UserEntity u = userRepository.findByUsername(username);
        if (u == null) {
            log.warn("Usuario no encontrado por username: {}", username);
            throw new UserNotFoundException("User not found with username " + username);
        }
        return u;
    }

    @Override
    @Transactional
    public UserEntity updateUsername(Integer userId, String newUsername) {
        log.info("Actualizando username para userId {} -> {}", userId, newUsername);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        if (userRepository.existsByUsername(newUsername)) {
            log.warn("Username conflict al actualizar userId {} a {}", userId, newUsername);
            throw new UsernameConflictException();
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
        if (!userRepository.existsById(userId)) {
            log.warn("Usuario no encontrado al consultar following: {}", userId);
            throw new UserNotFoundException("User not found with id " + userId);
        }
        return userRepository.findFollowingByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> getFollowers(Integer userId, Pageable pageable) {
        log.info("Obteniendo followers de userId {}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("Usuario no encontrado al consultar followers: {}", userId);
            throw new UserNotFoundException("User not found with id " + userId);
        }
        return userRepository.findFollowersByUserId(userId, pageable);
    }
}
