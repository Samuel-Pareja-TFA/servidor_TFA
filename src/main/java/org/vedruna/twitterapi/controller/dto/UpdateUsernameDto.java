package org.vedruna.twitterapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para actualizar únicamente el nombre de usuario.
 *
 * <p>Se utiliza en el endpoint {@code PATCH /api/v1/users/{userId}/username}.</p>
 *
 * <p>Validación:
 * <ul>
 *   <li>{@code username} no puede estar vacío.</li>
 * </ul>
 * </p>
 */
@Data
@Schema(description = "DTO para actualizar solo el username")
public class UpdateUsernameDto {

    @Schema(description = "Nuevo nombre de usuario", example = "usuarioEditado")
    @NotBlank(message = "username is required")
    private String username;
}
