package org.vedruna.twitterapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;

/**
 * Contrato para la lógica de negocio de publicaciones.
 */
public interface PublicationService {

    Page<PublicationEntity> getAllPublications(Pageable pageable);

    Page<PublicationEntity> getPublicationsByUser(Integer userId, Pageable pageable);

    /**
     * Obtener una publicación por su id (útil para operaciones de lectura/edición).
     */
    PublicationEntity getPublicationById(Integer publicationId);

    /**
     * Obtener las publicaciones de los usuarios que sigue el usuario userId.
     */
    Page<PublicationEntity> getPublicationsOfFollowing(Integer userId, Pageable pageable);

    PublicationEntity createPublication(PublicationEntity publication);

    PublicationEntity updatePublication(Integer publicationId, PublicationEntity publication);

    void deletePublication(Integer publicationId);
}
