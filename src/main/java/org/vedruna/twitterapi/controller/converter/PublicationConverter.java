package org.vedruna.twitterapi.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.vedruna.twitterapi.controller.dto.CreatePublicationDto;
import org.vedruna.twitterapi.controller.dto.PublicationDto;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;

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
     * Se asume que el Service asignará el user real (UserEntity) antes de persistir.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // el service debe buscar y setear el user
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    PublicationEntity toEntity(CreatePublicationDto dto);
}
