package org.vedruna.twitterapi.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.PublicationRepository;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.PublicationService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de PublicationService.
 */
@Slf4j
@AllArgsConstructor
@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PublicationEntity> getAllPublications(Pageable pageable) {
        log.info("Obteniendo todas las publicaciones (paged)");
        return publicationRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PublicationEntity> getPublicationsByUser(Integer userId, Pageable pageable) {
        log.info("Obteniendo publicaciones de userId {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id " + userId);
        }
        return publicationRepository.findAllByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PublicationEntity> getPublicationsOfFollowing(Integer userId, Pageable pageable) {
        log.info("Obteniendo publicaciones de los usuarios que sigue userId {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        // convertimos Set<UserEntity> following a List<UserEntity>
        List<UserEntity> following = user.getFollowing() == null
                ? List.of()
                : user.getFollowing().stream().collect(Collectors.toList());

        return publicationRepository.findAllByUserIn(following, pageable);
    }

    @Override
    @Transactional
    public PublicationEntity createPublication(PublicationEntity publication) {
        log.info("Creando publicación para userId {}", publication.getUser() != null ? publication.getUser().getId() : null);

        if (publication.getUser() == null || publication.getUser().getId() == null) {
            throw new IllegalArgumentException("Publication must contain user id");
        }

        Integer userId = publication.getUser().getId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

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
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id " + publicationId));

        // Solo el texto y update_date (según requisitos): no tocar user ni createDate
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
            throw new EntityNotFoundException("Publication not found with id " + publicationId);
        }
        publicationRepository.deleteById(publicationId);
    }
}
