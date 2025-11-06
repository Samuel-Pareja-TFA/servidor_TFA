package org.vedruna.twitterapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para actualizar una publicación. Solo permite cambiar el texto.
 */
@Data
@Schema(description = "DTO para editar una publicación (solo text)")
public class UpdatePublicationDto {

    @Schema(description = "Texto de la publicación", example = "Texto actualizado")
    @NotBlank(message = "text is required")
    private String text;
}
