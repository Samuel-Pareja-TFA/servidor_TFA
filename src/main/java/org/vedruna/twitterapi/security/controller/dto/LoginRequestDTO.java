package org.vedruna.twitterapi.security.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las credenciales enviadas durante el login.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Credenciales para autenticación (login)")
public class LoginRequestDTO {

    @Schema(description = "Nombre de usuario", example = "Samuel", required = true)
    String username;

    @Schema(description = "Contraseña en texto (se compara con el hash en la BD)", example = "MiPass123!", required = true)
    String password;
}
