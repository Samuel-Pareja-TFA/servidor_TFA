package org.vedruna.twitterapi.security.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de registro de un nuevo usuario.
 * Se espera: username, password, email y una descripción opcional.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos necesarios para registrar un nuevo usuario")
public class RegisterRequestDTO {
    @Schema(description = "Nombre de usuario (único)", example = "pepito1234", required = true)
    String username;

    @Schema(description = "Contraseña en texto (será encriptada con bcrypt en el servicio)", example = "MiPass123!", required = true)
    String password;

    @Schema(description = "Correo electrónico válido del usuario", example = "pepito1234@example.com", required = true)
    String email;

    @Schema(description = "Descripción o biografía opcional del usuario", example = "Un usuario nuevo para pruebas")
    String description;
}
