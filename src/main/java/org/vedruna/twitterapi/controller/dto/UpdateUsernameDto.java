package org.vedruna.twitterapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para actualizar únicamente el username de un usuario.
 * Usado en el endpoint PATCH /api/v1/users/{userId}/username
 */
@Data
@Schema(description = "DTO para actualizar solo el username")
public class UpdateUsernameDto {

    @Schema(description = "Nuevo nombre de usuario", example = "usuarioEditado")
    @NotBlank(message = "username is required")
    private String username;
}
