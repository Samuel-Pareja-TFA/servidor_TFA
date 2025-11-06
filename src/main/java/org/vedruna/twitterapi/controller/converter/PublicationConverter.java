package org.vedruna.twitterapi.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import org.vedruna.twitterapi.controller.dto.CreatePublicationDto;
import org.vedruna.twitterapi.controller.dto.PublicationDto;
import org.vedruna.twitterapi.controller.dto.UpdatePublicationDto;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;
import org.vedruna.twitterapi.persistance.entity.UserEntity;


/**
 * Mapper para Publication <-> DTO.
 */
@Mapper(componentModel = "spring")
public interface PublicationConverter {

    /**
     * Mapea PublicationEntity -> PublicationDto
     * user.id -> userId
     * user.username -> username
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    PublicationDto toDto(PublicationEntity entity);

    /**
     * Convierte CreatePublicationDto -> PublicationEntity.
     * Se asume que el Service asignará el user real (UserEntity) antes de persistir,
     * pero aquí pedimos a MapStruct que cree una UserEntity mínima con solo el id
     * a partir de dto.userId para que publication.getUser().getId() no sea null.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    PublicationEntity toEntity(CreatePublicationDto dto);

    // Nuevo: mapear UpdatePublicationDto -> PublicationEntity (solo text)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // la entidad existente debe mantener su user
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    PublicationEntity toEntity(UpdatePublicationDto dto);
}
