package org.vedruna.twitterapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

/**
 * Contrato para la lógica de negocio relacionada con usuarios.
 */
public interface UserService {

    /**
     * Crea (registra) un nuevo usuario.
     * @param user entidad a crear (la contraseña debe llegar ya cifrada cuando sea necesario).
     * @return usuario creado.
     */
    UserEntity createUser(UserEntity user);

    /**
     * Obtener un usuario por id.
     * @throws jakarta.persistence.EntityNotFoundException si no existe.
     */
    UserEntity getUserById(Integer userId);

    /**
     * Obtener un usuario por username.
     * @throws jakarta.persistence.EntityNotFoundException si no existe.
     */
    UserEntity getUserByUsername(String username);

    /**
     * Editar únicamente el username de un usuario.
     * @param userId id del usuario a modificar.
     * @param newUsername nuevo username.
     * @return usuario actualizado.
     */
    UserEntity updateUsername(Integer userId, String newUsername);

    /**
     * Obtener usuarios que sigue un usuario (paged).
     */
    Page<UserEntity> getFollowing(Integer userId, Pageable pageable);

    /**
     * Obtener seguidores de un usuario (paged).
     */
    Page<UserEntity> getFollowers(Integer userId, Pageable pageable);
}
