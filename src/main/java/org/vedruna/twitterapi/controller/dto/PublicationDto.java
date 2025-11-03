package org.vedruna.twitterapi.controller.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO de respuesta para publicaciones.
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
