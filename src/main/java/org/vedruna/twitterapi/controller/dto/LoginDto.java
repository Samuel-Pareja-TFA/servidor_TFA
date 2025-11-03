package org.vedruna.twitterapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para hacer login (público).
 */
@Data
@Schema(description = "DTO para login")
public class LoginDto {

    @Schema(description = "Nombre de usuario", example = "juan01", required = true)
    @NotBlank(message = "username is required")
    private String username;

    @Schema(description = "Contraseña", example = "P@ssw0rd!", required = true)
    @NotBlank(message = "password is required")
    @Size(min = 8, max = 60)
    private String password;
}
