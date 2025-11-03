package org.vedruna.twitterapi.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.vedruna.twitterapi.controller.dto.CreateUserDto;
import org.vedruna.twitterapi.controller.dto.UserDto;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

/**
 * MapStruct mapper para User <-> DTO.
 * MapStruct genera la implementación en compile-time.
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * Convierte entidad a DTO.
     * Mapea role.name -> roleName si existe role.
     */
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "role.name", target = "roleName", defaultValue = "")
    UserDto toDto(UserEntity entity);

    /**
     * Convierte DTO de creación a entidad.
     * NOTA: no seteamos createDate ni role aquí, lo debe hacer el Service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "publications", ignore = true) // ignoramos la lista de publicaciones
    @Mapping(target = "following", ignore = true)    // ignoramos relaciones many-to-many
    @Mapping(target = "followers", ignore = true)
    UserEntity toEntity(CreateUserDto dto);
}
