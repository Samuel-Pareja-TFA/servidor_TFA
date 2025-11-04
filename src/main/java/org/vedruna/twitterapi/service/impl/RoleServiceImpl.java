package org.vedruna.twitterapi.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.vedruna.twitterapi.persistance.entity.RoleEntity;
import org.vedruna.twitterapi.persistance.repository.RoleRepository;
import org.vedruna.twitterapi.service.RoleService;
import org.vedruna.twitterapi.service.exception.RoleNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación mínima de RoleService.
 */
@Slf4j
@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Optional<RoleEntity> findByName(String name) {
        log.info("Buscando rol por name {}", name);
        return roleRepository.findByName(name);
    }

    /**
     * Helper: obtener rol por nombre o lanzar RoleNotFoundException
     */
    public RoleEntity getByNameOrThrow(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name " + name));
    }
}
