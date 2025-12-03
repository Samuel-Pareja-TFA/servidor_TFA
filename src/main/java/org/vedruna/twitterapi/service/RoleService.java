package org.vedruna.twitterapi.service;

import java.util.Optional;
import org.vedruna.twitterapi.persistance.entity.RoleEntity;

/**
 * Servicio (contrato) que define las operaciones relacionadas con roles en
 * la aplicación.
 *
 * <p>Responsabilidad: abstraer la lógica de acceso y gestión de roles para
 * la capa de negocio. Implementaciones de esta interfaz deben encargarse de
 * interactuar con el repositorio de roles, aplicar validaciones y devolver
 * resultados en estructuras adecuadas (por ejemplo {@link Optional} cuando
 * la consulta puede no devolver una entidad).</p>
 *
 * <p>Consideraciones:</p>
 * <ul>
 *   <li>Usar {@link Optional} en los métodos de búsqueda ayuda a expresar de
 *       forma explícita que el resultado puede no existir y evita devolver
 *       {@code null}.</li>
 *   <li>La implementación debe decidir cómo mapear errores (por ejemplo,
 *       lanzar excepciones custom en operaciones de creación/actualización si
 *       hay violaciones de integridad).</li>
 * </ul>
 */
public interface RoleService {

    /**
     * Busca un rol por su nombre.
     *
     * <p>Entrada: nombre del rol tal y como se almacena en la base de datos
     * (por ejemplo {@code ROLE_USER} o {@code ROLE_ADMIN}). La búsqueda se
     * realiza típicamente de forma exacta y case-sensitive a menos que la
     * implementación especifique lo contrario.</p>
     *
     * <p>Salida: un {@link Optional} que contendrá la entidad {@link
     * RoleEntity} si existe, o vacío si no se encuentra ningún rol con ese
     * nombre. Esto permite al llamador decidir cómo actuar (crear el rol,
     * devolver 404 en un endpoint, etc.).</p>
     *
     * @param name nombre del rol a buscar (no debe ser {@code null}).
     * @return {@link Optional} con el {@link RoleEntity} encontrado o vacío si
     *         no existe.
     */
    Optional<RoleEntity> findByName(String name);
}
