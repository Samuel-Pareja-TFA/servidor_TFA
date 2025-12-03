package org.vedruna.twitterapi.security.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene el refresh token que el cliente envía para obtener
 * un nuevo access token.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Cuerpo para solicitar renovación de access token usando refresh token")
public class RefreshRequestDTO {

    @Schema(description = "Refresh token previamente emitido", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    String refreshToken;
}
