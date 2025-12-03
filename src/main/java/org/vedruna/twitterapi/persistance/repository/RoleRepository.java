package org.vedruna.twitterapi.persistance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vedruna.twitterapi.persistance.entity.RoleEntity;

/**
 * Repositorio Spring Data JPA para la entidad {@link RoleEntity}.
 *
 * <p>Extiende {@link JpaRepository} para proporcionar operaciones CRUD
 * estándar (buscar por id, guardar, eliminar, paginar, etc.) y añade
 * consultas específicas necesarias en la aplicación, por ejemplo
 * {@link #findByName(String)}.</p>
 *
 * <p>Responsabilidad: abstraer el acceso a datos para roles, permitiendo a
 * la capa de servicio realizar operaciones sin depender de la API JPA
 * directamente.</p>
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    /**
     * Busca un rol por su nombre.
     *
     * <p>Este método utiliza el mecanismo de query derivation de Spring Data
     * JPA: a partir del nombre del método {@code findByName} se construye la
     * consulta equivalente a {@code SELECT r FROM RoleEntity r WHERE
     * r.name = :name}.</p>
     *
     * @param name nombre del rol a buscar (ej. {@code ROLE_USER}). No debe ser
     *             {@code null} para obtener un resultado significativo.
     * @return {@link Optional} con el {@link RoleEntity} encontrado o vacío si
     *         no existe un rol con ese nombre.
     */
    Optional<RoleEntity> findByName(String name);
}
