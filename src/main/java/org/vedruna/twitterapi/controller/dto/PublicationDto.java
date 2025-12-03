package org.vedruna.twitterapi.controller.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO de respuesta para publicaciones.
 *
 * <p>Incluye información básica de la publicación y del autor para mostrar en listas y timelines.</p>
 *
 * <p>Campos:
 * <ul>
 *   <li>{@code id}: Identificador de la publicación.</li>
 *   <li>{@code userId}: Identificador del autor.</li>
 *   <li>{@code username}: Nombre del usuario autor.</li>
 *   <li>{@code text}: Contenido de la publicación.</li>
 *   <li>{@code createDate}: Fecha de creación.</li>
 *   <li>{@code updateDate}: Fecha de última modificación.</li>
 * </ul>
 * </p>
 */
@Data
@Schema(description = "DTO de publicación")
public class PublicationDto {

    @Schema(description = "Id de la publicación")
    private Integer id;

    @Schema(description = "Id del autor")
    private Integer userId;

    @Schema(description = "Nombre de usuario del autor")
    private String username;

    @Schema(description = "Texto de la publicación")
    private String text;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createDate;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updateDate;
}
