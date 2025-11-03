package org.vedruna.twitterapi.controller.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.twitterapi.controller.PublicationController;
import org.vedruna.twitterapi.controller.converter.PublicationConverter;
import org.vedruna.twitterapi.controller.dto.CreatePublicationDto;
import org.vedruna.twitterapi.controller.dto.PublicationDto;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;
import org.vedruna.twitterapi.service.PublicationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación REST del PublicationController.
 */
@AllArgsConstructor
@CrossOrigin
@RestController
@Slf4j
public class PublicationControllerImpl implements PublicationController {

    private final PublicationService publicationService;
    private final PublicationConverter publicationConverter;

    @Override
    public ResponseEntity<Page<PublicationDto>> getAllPublications(Pageable pageable) {
        log.info("Get all publications");
        Page<PublicationEntity> page = publicationService.getAllPublications(pageable);
        return ResponseEntity.ok(page.map(publicationConverter::toDto));
    }

    @Override
    public ResponseEntity<Page<PublicationDto>> getPublicationsByUser(Integer userId, Pageable pageable) {
        log.info("Get publications by user {}", userId);
        Page<PublicationEntity> page = publicationService.getPublicationsByUser(userId, pageable);
        return ResponseEntity.ok(page.map(publicationConverter::toDto));
    }

    @Override
    public ResponseEntity<Page<PublicationDto>> getTimeline(Integer userId, Pageable pageable) {
        log.info("Get timeline for user {}", userId);
        Page<PublicationEntity> page = publicationService.getPublicationsOfFollowing(userId, pageable);
        return ResponseEntity.ok(page.map(publicationConverter::toDto));
    }

    @Override
    public ResponseEntity<PublicationDto> createPublication(@Valid CreatePublicationDto dto) {
        log.info("Create publication for user {}", dto.getUserId());
        PublicationEntity entity = publicationConverter.toEntity(dto);
        PublicationEntity saved = publicationService.createPublication(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicationConverter.toDto(saved));
    }

    @Override
    public ResponseEntity<PublicationDto> updatePublication(Integer publicationId, @Valid CreatePublicationDto dto) {
        log.info("Update publication {}", publicationId);
        PublicationEntity entity = publicationConverter.toEntity(dto);
        PublicationEntity updated = publicationService.updatePublication(publicationId, entity);
        return ResponseEntity.ok(publicationConverter.toDto(updated));
    }

    @Override
    public ResponseEntity<Void> deletePublication(Integer publicationId) {
        log.info("Delete publication {}", publicationId);
        publicationService.deletePublication(publicationId);
        return ResponseEntity.noContent().build();
    }
}
