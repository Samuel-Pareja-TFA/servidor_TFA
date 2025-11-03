package org.vedruna.twitterapi.controller.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para devolver información pública del usuario.
 * No incluye la contraseña.
 */
@Data
@Schema(description = "DTO público de usuario (no incluye password)")
public class UserDto {

    @Schema(description = "Id del usuario en la BD", example = "1")
    private Integer userId;

    @Schema(description = "Nombre de usuario", example = "juan01")
    private String username;

    @Schema(description = "Email del usuario (puedes omitir este campo si lo consideras sensible)", example = "juan@example.com")
    private String email;

    @Schema(description = "Descripción del perfil", example = "Amante de la música")
    private String description;

    @Schema(description = "Fecha de creación del usuario", example = "2025-01-02")
    private LocalDate createDate;

    @Schema(description = "Nombre del rol del usuario", example = "Usuario")
    private String roleName;
}
