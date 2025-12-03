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
 * Mapper de MapStruct para convertir entre {@link PublicationEntity} y sus DTOs.
 *
 * <p>Este mapper permite:
 * <ul>
 *   <li>Convertir {@link PublicationEntity} a {@link PublicationDto} para exponer publicaciones.</li>
 *   <li>Convertir {@link CreatePublicationDto} a {@link PublicationEntity} para crear nuevas publicaciones.</li>
 *   <li>Convertir {@link UpdatePublicationDto} a {@link PublicationEntity} para actualizar solo el texto de publicaciones existentes.</li>
 * </ul>
 * </p>
 *
 * <p>Consideraciones:
 * <ul>
 *   <li>Al mapear a DTO, se incluyen los campos {@code userId} y {@code username} obtenidos de la entidad {@link UserEntity} asociada.</li>
 *   <li>Al mapear desde DTOs de creación o actualización, se ignoran fechas de creación y actualización
 *       y la referencia completa a {@code UserEntity} porque se gestiona en la capa de servicio.</li>
 * </ul>
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PublicationConverter {

    /**
     * Convierte una entidad {@link PublicationEntity} a su representación {@link PublicationDto}.
     *
     * <p>Mapeos específicos:
     * <ul>
     *   <li>{@code user.id} -> {@code userId}</li>
     *   <li>{@code user.username} -> {@code username}</li>
     *   <li>Otros campos simples ({@code id, text, createDate, updateDate}) se copian directamente.</li>
     * </ul>
     * </p>
     *
     * @param entity la entidad de publicación a convertir
     * @return DTO de publicación
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    PublicationDto toDto(PublicationEntity entity);

    /**
     * Convierte un {@link CreatePublicationDto} a {@link PublicationEntity}.
     *
     * <p>Se asume que el usuario asociado ({@link UserEntity}) será asignado en la capa de servicio antes de persistir.</p>
     *
     * <p>Campos ignorados:
     * <ul>
     *   <li>{@code id}: generado automáticamente por la BD.</li>
     *   <li>{@code createDate} y {@code updateDate}: asignados por el Service al persistir.</li>
     * </ul>
     * </p>
     *
     * @param dto DTO de creación de publicación
     * @return entidad {@link PublicationEntity} lista para persistir
     */
    @Mapping(target = "id", ignore = true)
    // @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    PublicationEntity toEntity(CreatePublicationDto dto);

        /**
     * Convierte un {@link UpdatePublicationDto} a {@link PublicationEntity}.
     *
     * <p>Se utiliza para actualizar solo el campo {@code text} de publicaciones existentes.
     * La entidad devuelta mantiene su usuario original y fechas de creación/actualización
     * se gestionan en el Service.</p>
     *
     * <p>Campos ignorados:
     * <ul>
     *   <li>{@code id}</li>
     *   <li>{@code user}</li>
     *   <li>{@code createDate}</li>
     *   <li>{@code updateDate}</li>
     * </ul>
     * </p>
     *
     * @param dto DTO con el texto actualizado
     * @return entidad {@link PublicationEntity} lista para aplicar cambios
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // la entidad existente debe mantener su user
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    PublicationEntity toEntity(UpdatePublicationDto dto);
}
