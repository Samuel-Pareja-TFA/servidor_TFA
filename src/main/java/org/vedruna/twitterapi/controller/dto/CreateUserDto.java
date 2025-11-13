package org.vedruna.twitterapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para registrar un nuevo usuario.
 *
 * <p>Se utiliza en el endpoint {@code POST /api/v1/users/register}.</p>
 *
 * <p>Validaciones:
 * <ul>
 *   <li>{@code username} obligatorio, único, longitud entre 3 y 20 caracteres.</li>
 *   <li>{@code password} obligatorio, longitud entre 8 y 60 caracteres.</li>
 *   <li>{@code email} obligatorio, formato de email válido, longitud máxima 90.</li>
 *   <li>{@code description} opcional, longitud máxima 2000.</li>
 * </ul>
 * </p>
 */
@Data
@Schema(description = "DTO para crear un usuario (registro)")
public class CreateUserDto {

    @Schema(description = "Nombre de usuario. Único en la BD", example = "juan01", required = true)
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 20, message = "username must be between 3 and 20 characters")
    private String username;

    @Schema(description = "Contraseña del usuario (bcrypt length recommend 60)", example = "P@ssw0rd!", required = true)
    @NotBlank(message = "password is required")
    @Size(min = 8, max = 60, message = "password must be between 8 and 60 characters")
    private String password;

    @Schema(description = "Email del usuario", example = "juan@example.com", required = true)
    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    @Size(max = 90, message = "email max length is 90")
    private String email;

    @Schema(description = "Descripción opcional del usuario", example = "Me encanta programar", required = false)
    @Size(max = 2000, message = "description max length is 2000")
    private String description;
}
