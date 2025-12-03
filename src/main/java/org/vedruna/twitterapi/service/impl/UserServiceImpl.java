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
 * Implementación concreta de la interfaz {@link org.vedruna.twitterapi.service.UserService}.
 *
 * <p>Responsabilidades principales:
 * <ul>
 *   <li>Contener la lógica de negocio relacionada con usuarios (creación, consulta y actualizaciones
 *       parciales como el renombre de username).</li>
 *   <li>Aplicar validaciones de unicidad (username, email) y delegar búsqueda/obtención de roles
 *       al {@link org.vedruna.twitterapi.service.RoleService}.</li>
 *   <li>Manejar timestamps básicos (asignar createDate si no está presente) y coordinar persistencia
 *       a través de {@link org.vedruna.twitterapi.persistance.repository.UserRepository}.</li>
 * </ul>
 *
 * <p>Notas de diseño y contraints:
 * <ul>
 *   <li>Los métodos realizan validaciones básicas y lanzan excepciones de servicio específicas
 *       (por ejemplo {@link org.vedruna.twitterapi.service.exception.UsernameConflictException},
 *       {@link org.vedruna.twitterapi.service.exception.EmailConflictException} y
 *       {@link org.vedruna.twitterapi.service.exception.UserNotFoundException}).</li>
 *   <li>Se usan transacciones de Spring declarativas (@Transactional). Los métodos de lectura marcan
 *       readOnly=true para optimizar la interacción con el contexto de persistencia.</li>
 *   <li>Esta clase asume que las entidades pasadas como parámetros (por ejemplo en createUser)
 *       contienen la información mínima requerida; la validación y normalización adicionales deben
 *       aplicarse en capas superiores si es necesario (controladores o validadores dedicados).</li>
 * </ul>
 *
 * <p>Errores y excepciones: las excepciones lanzadas por este servicio son chequeadas por la capa
 * superior (controladores/handlers) y se espera que sean mapeadas a respuestas HTTP apropiadas.
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    /**
     * Repositorio para operaciones CRUD sobre {@link org.vedruna.twitterapi.persistance.entity.UserEntity}.
     *
     * <p>Usado para búsquedas, comprobaciones de existencia y persistencia de la entidad User.
     * No debe ser accedido directamente por capas de presentación/servicio externas a este bean.
     */
    private final UserRepository userRepository;

    /**
     * Servicio de roles usado para resolver y asignar roles por nombre.
     *
     * <p>Se delegan búsquedas de rol por nombre a este servicio para mantener la responsabilidad
     * separada y permitir caching/optimización a nivel de roles si se implementa más adelante.
     */
    private final RoleService roleService;

    @Override
    @Transactional
    /**
     * Crea y persiste un nuevo {@link UserEntity} aplicando validaciones básicas.
     *
     * <p>Comportamiento detallado:
     * <ol>
     *   <li>Valida que no exista otro usuario con el mismo username; si existe lanza
     *       {@link org.vedruna.twitterapi.service.exception.UsernameConflictException}.</li>
     *   <li>Valida que no exista otro usuario con el mismo email; si existe lanza
     *       {@link org.vedruna.twitterapi.service.exception.EmailConflictException}.</li>
     *   <li>Si no se proporciona {@code createDate} en la entidad, asigna la fecha actual.</li>
     *   <li>Si no se proporcionó rol, intenta asignar el rol por defecto con nombre "Usuario".
     *       Si el rol por defecto no existe lanza
     *       {@link org.vedruna.twitterapi.service.exception.RoleNotFoundException}.</li>
     *   <li>Persiste la entidad mediante {@link #userRepository} y devuelve la entidad persistida
     *       con su id generado.</li>
     * </ol>
     *
     * @param user entidad de usuario con los datos a persistir. Debe contener al menos
     *             username y email válidos; otras validaciones (formato, longitud) deben
     *             realizarse en capas superiores si se requieren.
     * @return la entidad {@code UserEntity} ya persistida (con id y campos generados).
     * @throws UsernameConflictException si ya existe un usuario con el mismo username.
     * @throws EmailConflictException si ya existe un usuario con el mismo email.
     * @throws RoleNotFoundException si no se encuentra el rol por defecto requerido.
     */
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
    /**
     * Recupera un usuario por su identificador numérico.
     *
     * @param userId id del usuario a buscar. No debe ser {@code null}.
     * @return la entidad {@link UserEntity} encontrada.
     * @throws UserNotFoundException si no existe un usuario con el id proporcionado.
     */
    public UserEntity getUserById(Integer userId) {
        log.info("Buscando usuario por id {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * Recupera un usuario por su nombre de usuario (username).
     *
     * <p>Esta operación lanza {@link UserNotFoundException} si no se encuentra el usuario.
     *
     * @param username nombre de usuario a buscar; no debe ser {@code null} ni vacío.
     * @return la entidad {@link UserEntity} correspondiente al username.
     * @throws UserNotFoundException si no existe un usuario con el username proporcionado.
     */
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
    /**
     * Actualiza el campo {@code username} de un usuario existente.
     *
     * <p>Comportamiento y requisitos:
     * <ul>
     *   <li>Verifica que el usuario exista; si no existe lanza {@link UserNotFoundException}.</li>
     *   <li>Comprueba que el nuevo username no esté en uso por otro usuario; si existe lanza
     *       {@link UsernameConflictException}.</li>
     *   <li>Persiste y devuelve la entidad actualizada.</li>
     * </ul>
     *
     * @param userId id del usuario cuyo username se desea actualizar.
     * @param newUsername nuevo username propuesto; no debe ser {@code null} ni vacío.
     * @return la entidad {@link UserEntity} actualizada con el nuevo username.
     * @throws UserNotFoundException si no existe el usuario con {@code userId}.
     * @throws UsernameConflictException si {@code newUsername} ya está en uso.
     */
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
    /**
     * Recupera la página de usuarios que el usuario identificado por {@code userId} sigue.
     *
     * <p>Observaciones:
     * <ul>
     *   <li>Valida existencia del usuario origen; si no existe lanza
     *       {@link UserNotFoundException}.</li>
     *   <li>La paginación se delega a Spring Data mediante el parámetro {@code pageable}.</li>
     *   <li>Retorna una {@link org.springframework.data.domain.Page} conteniendo
     *       {@link UserEntity} de los usuarios seguidos.</li>
     * </ul>
     *
     * @param userId id del usuario origen (quien sigue a otros).
     * @param pageable objeto de paginación y ordenación; obligatorio.
     * @return página de {@link UserEntity} que el usuario sigue.
     * @throws UserNotFoundException si no existe el usuario origen.
     */
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
    /**
     * Recupera la página de usuarios que siguen al usuario identificado por {@code userId}.
     *
     * <p>Comportamientos idénticos a {@link #getFollowing(Integer, Pageable)} salvo que el
     * sentido de la relación es inverso (followers en lugar de following).
     *
     * @param userId id del usuario destino (quien es seguido por otros).
     * @param pageable objeto de paginación y ordenación; obligatorio.
     * @return página de {@link UserEntity} que siguen al usuario dado.
     * @throws UserNotFoundException si no existe el usuario destino.
     */
    public Page<UserEntity> getFollowers(Integer userId, Pageable pageable) {
        log.info("Obteniendo followers de userId {}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("Usuario no encontrado al consultar followers: {}", userId);
            throw new UserNotFoundException("User not found with id " + userId);
        }
        return userRepository.findFollowersByUserId(userId, pageable);
    }
}
