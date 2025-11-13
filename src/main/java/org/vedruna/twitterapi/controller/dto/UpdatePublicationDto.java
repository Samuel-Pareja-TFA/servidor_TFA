package org.vedruna.twitterapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para actualizar el texto de una publicación existente.
 *
 * <p>Se utiliza en endpoints de actualización de publicaciones (solo permite editar el contenido).</p>
 *
 * <p>Validación:
 * <ul>
 *   <li>{@code text} no puede estar vacío.</li>
 * </ul>
 * </p>
 */
@Data
@Schema(description = "DTO para editar una publicación (solo text)")
public class UpdatePublicationDto {

    @Schema(description = "Texto de la publicación", example = "Texto actualizado")
    @NotBlank(message = "text is required")
    private String text;
}
