package org.vedruna.twitterapi.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.PublicationRepository;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.PublicationService;
import org.vedruna.twitterapi.service.exception.PublicationConflictException;
import org.vedruna.twitterapi.service.exception.PublicationNotFoundException;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio de publicaciones de la aplicación.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Proveer operaciones CRUD sobre {@link PublicationEntity}.</li>
 *   <li>Validar precondiciones de negocio (existencia de usuario, integridad de la entidad
 *       antes de persistir, manejo de timestamps).</li>
 *   <li>Delegar consultas paginadas al repositorio y encapsular excepciones de dominio
 *       como {@link PublicationNotFoundException} o {@link PublicationConflictException}.</li>
 * </ul>
 *
 * <p>Notas de implementación:
 * <ul>
 *   <li>Las operaciones de lectura usan transacciones marcadas como {@code readOnly=true} para
 *       optimizar el acceso a la base de datos.</li>
 *   <li>Los métodos de escritura gestionan timestamps locales con {@link LocalDateTime#now()} y
 *       preservan la integridad referencial resolviendo entidades relacionadas (por ejemplo
 *       {@link UserEntity}) antes de persistir.</li>
 * </ul>
 */
@Slf4j
@AllArgsConstructor
@Service
public class PublicationServiceImpl implements PublicationService {

    /** Repositorio para publicaciones. */
    private final PublicationRepository publicationRepository;

    /** Repositorio para usuarios (usado para validar existencia y resolver relaciones). */
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Page<PublicationEntity> getAllPublications(Pageable pageable) {
        log.info("Obteniendo todas las publicaciones (paged)");
        return publicationRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PublicationEntity> getPublicationsByUser(Integer userId, Pageable pageable) {
        log.info("Obteniendo publicaciones de userId {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id " + userId);
        }
        return publicationRepository.findAllByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicationEntity getPublicationById(Integer publicationId) {
    log.info("Obteniendo publicación id {}", publicationId);
    return publicationRepository.findById(publicationId)
        .orElseThrow(() -> new PublicationNotFoundException("Publication not found with id " + publicationId));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PublicationEntity> getPublicationsOfFollowing(Integer userId, Pageable pageable) {
        log.info("Obteniendo publicaciones de los usuarios que sigue userId {}", userId);

        // verificamos existencia del usuario (lanza UserNotFoundException si no existe)
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id " + userId);
        }

        // Usamos un query que hace JOIN en BD y evita inicializar colecciones en entidades
        return publicationRepository.findAllByUserInFollowing(userId, pageable);
    }

    @Override
    @Transactional
    public PublicationEntity createPublication(PublicationEntity publication) {
        log.info("Creando publicación para userId {}", publication.getUser() != null ? publication.getUser().getId() : null);

        if (publication.getUser() == null || publication.getUser().getId() == null) {
            throw new PublicationConflictException("Publication must contain user id");
        }

        Integer userId = publication.getUser().getId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        publication.setUser(user);
        publication.setCreateDate(LocalDateTime.now());
        PublicationEntity saved = publicationRepository.save(publication);
        log.info("Publicación creada con id {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public PublicationEntity updatePublication(Integer publicationId, PublicationEntity publication) {
    log.info("Actualizando publicación id {}", publicationId);
    PublicationEntity existing = publicationRepository.findById(publicationId)
        .orElseThrow(() -> new PublicationNotFoundException("Publication not found with id " + publicationId));

    existing.setText(publication.getText());
    existing.setUpdateDate(LocalDateTime.now());

    PublicationEntity updated = publicationRepository.save(existing);
    log.info("Publicación actualizada id {}", updated.getId());
    return updated;
    }

    @Override
    @Transactional
    public void deletePublication(Integer publicationId) {
        log.info("Borrando publicación id {}", publicationId);
        if (!publicationRepository.existsById(publicationId)) {
            throw new PublicationNotFoundException("Publication not found with id " + publicationId);
        }
        publicationRepository.deleteById(publicationId);
    }
}
