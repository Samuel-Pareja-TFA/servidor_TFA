package org.vedruna.twitterapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para crear o actualizar una publicación.
 *
 * <p>Se utiliza en los endpoints de creación y edición de publicaciones.
 * Solo permite establecer el contenido ({@code text}) de la publicación.</p>
 *
 * <p>Validaciones:
 * <ul>
 *   <li>{@code text} obligatorio, longitud entre 1 y 280 caracteres.</li>
 * </ul>
 * </p>
 */
@Data
@Schema(description = "DTO para crear o editar una publicación")
public class CreatePublicationDto {

    // @Schema(description = "Id del usuario autor de la publicación", example = "2", required = true)
    // @NotNull(message = "userId is required")
    // private Integer userId;

    @Schema(description = "Contenido de la publicación (max 280 chars)", example = "Mi primera publicación!", required = true)
    @NotBlank(message = "text is required")
    @Size(min = 1, max = 280, message = "text must be between 1 and 280 characters")
    private String text;
}
