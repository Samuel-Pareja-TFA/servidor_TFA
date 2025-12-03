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
 * Implementación del servicio de roles.
 *
 * <p>Este bean encapsula operaciones de lectura/consulta sobre roles de aplicación
 * representados por {@link RoleEntity}. Está pensado como la capa que aísla el acceso
 * al repositorio {@link RoleRepository} y proporciona helpers convenientes para el resto
 * de la aplicación (por ejemplo, resolviendo o lanzando excepciones cuando un rol no existe).
 *
 * <p>Responsabilidades clave:
 * <ul>
 *   <li>Buscar roles por nombre (case-sensitive según la implementación del repositorio).</li>
 *   <li>Proveer un helper {@link #getByNameOrThrow(String)} que simplifica la recuperación
 *       de roles y la gestión de errores (lanza {@link RoleNotFoundException} cuando no existe).</li>
 * </ul>
 *
 * <p>Notas de implementación:
 * <ul>
 *   <li>Esta clase es deliberadamente pequeña y delega la persistencia a
 *       {@link RoleRepository} para mantener la capa de servicio ligera.</li>
 *   <li>Logging ligero para trazabilidad de operaciones de lectura.</li>
 * </ul>
 */
@Slf4j
@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    /**
     * Repositorio que expone operaciones para consultar entidades {@link RoleEntity}.
     *
     * <p>Se inyecta por constructor (Lombok @AllArgsConstructor). No se expone directamente
     * a capas superiores; todas las consultas deben pasar por este servicio para mantener
     * coherencia en el tratamiento de errores y logging.
     */
    private final RoleRepository roleRepository;

    /**
     * Busca un rol por su nombre.
     *
     * <p>Este método delega directamente en {@link RoleRepository#findByName(String)}
     * y devuelve un {@link Optional} que permite al llamador decidir el comportamiento
     * cuando el rol no existe.
     *
     * @param name nombre del rol a buscar; no debe ser {@code null} ni vacío.
     * @return {@link Optional} contendo el {@link RoleEntity} si existe, o {@link Optional#empty()} si no.
     */
    @Override
    public Optional<RoleEntity> findByName(String name) {
        log.info("Buscando rol por name {}", name);
        return roleRepository.findByName(name);
    }

    /**
     * Recupera un {@link RoleEntity} por nombre o lanza {@link RoleNotFoundException} si no existe.
     *
     * <p>Helper útil para casos donde el rol es obligatorio (por ejemplo al crear usuarios)
     * y conviene fail-fast con una excepción de dominio clara.
     *
     * @param name nombre del rol buscado.
     * @return el {@link RoleEntity} existente.
     * @throws RoleNotFoundException si no existe un rol con el nombre indicado.
     */
    public RoleEntity getByNameOrThrow(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name " + name));
    }
}
