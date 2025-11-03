package org.vedruna.twitterapi.service;

import java.util.Optional;
import org.vedruna.twitterapi.persistance.entity.RoleEntity;

/**
 * Interfaz mínima para operaciones con roles.
 */
public interface RoleService {
    Optional<RoleEntity> findByName(String name);
}
