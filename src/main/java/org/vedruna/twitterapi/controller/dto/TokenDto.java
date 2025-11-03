package org.vedruna.twitterapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO que devuelve el token JWT (cuando implementes auth).
 */
@Data
@AllArgsConstructor
@Schema(description = "DTO que contiene el token JWT")
public class TokenDto {
    @Schema(description = "JWT token string")
    private String token;
}
