package org.vedruna.twitterapi.security.controller.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO público que representa los datos visibles de un usuario.
 * No contiene información sensible como la contraseña.
 */
@Data
@Schema(description = "DTO público de usuario (no incluye password)")
public class UserDTO {
    @Schema(description = "Identificador único del usuario en la base de datos", example = "8")
    Integer userId;

    @Schema(description = "Nombre de usuario único", example = "Samuel")
    String username;

    @Schema(description = "Email del usuario", example = "samu@example.com")
    String email;

    @Schema(description = "Descripción / biografía del usuario", example = "Un usuario nuevo para samu")
    String description;

    @Schema(description = "Fecha de creación del usuario en formato ISO (YYYY-MM-DD)", example = "2025-11-12")
    LocalDate createDate;
}
