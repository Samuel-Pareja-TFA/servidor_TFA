package org.vedruna.twitterapi.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.vedruna.twitterapi.controller.dto.CreateUserDto;
import org.vedruna.twitterapi.controller.dto.UserDto;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

/**
 * Mapper de MapStruct para convertir entre {@link UserEntity} y DTOs relacionados.
 *
 * <p>Este mapper realiza la conversión bidireccional:
 * <ul>
 *   <li>{@link UserEntity} -> {@link UserDto}: Para exponer datos de usuario públicamente.</li>
 *   <li>{@link CreateUserDto} -> {@link UserEntity}: Para crear entidades a partir de datos recibidos en un registro.</li>
 * </ul>
 * </p>
 *
 * <p>Consideraciones:
 * <ul>
 *   <li>MapStruct genera la implementación en tiempo de compilación.</li>
 *   <li>Al convertir a {@link UserDto}, se mapea el nombre del rol (role.name) al campo {@code roleName}.</li>
 *   <li>Al convertir a {@link UserEntity}, se ignoran campos que serán gestionados por la capa de servicio
 *       ({@code createDate}, {@code role}, relaciones {@code following}, {@code followers} y {@code publications}).</li>
 * </ul>
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * Convierte una entidad {@link UserEntity} a su representación {@link UserDto}.
     *
     * <p>Mapeos específicos:
     * <ul>
     *   <li>{@code id} -> {@code userId}</li>
     *   <li>{@code role.name} -> {@code roleName} (si el usuario tiene rol asignado, sino se devuelve cadena vacía)</li>
     * </ul>
     * </p>
     *
     * @param entity la entidad de usuario a convertir
     * @return DTO de usuario
     */
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "role.name", target = "roleName", defaultValue = "")
    UserDto toDto(UserEntity entity);

    /**
     * Convierte un {@link CreateUserDto} a {@link UserEntity}.
     *
     * <p>Se ignoran campos gestionados automáticamente por la capa de persistencia o por la lógica de negocio:</p>
     * <ul>
     *   <li>{@code id}: se genera automáticamente al persistir.</li>
     *   <li>{@code createDate}: se asigna en el Service al crear usuario.</li>
     *   <li>{@code role}: se asigna en el Service o por defecto.</li>
     *   <li>{@code publications}: relaciones de publicaciones se gestionan por separado.</li>
     *   <li>{@code following} y {@code followers}: relaciones many-to-many se gestionan vía Service/Repository.</li>
     * </ul>
     *
     * @param dto DTO con los datos de creación de usuario
     * @return entidad {@link UserEntity} lista para persistir
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "publications", ignore = true) // ignoramos la lista de publicaciones
    @Mapping(target = "following", ignore = true)    // ignoramos relaciones many-to-many
    @Mapping(target = "followers", ignore = true)
    UserEntity toEntity(CreateUserDto dto);
}
