package org.vedruna.twitterapi.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta tras autenticación: contiene access token y refresh token.
 * El campo token_type se expone para seguir la convención OAuth (Bearer).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de autenticación con tokens")
public class AuthResponseDTO {

    @Schema(description = "Tipo de token (siempre 'Bearer')", example = "Bearer")
    @JsonProperty("token_type")
    final String tokenType = "Bearer";

    @Schema(description = "Access token JWT (corto plazo)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("access_token")
    String accessToken;

    @Schema(description = "Tiempo de expiración del access token en segundos", example = "180")
    @JsonProperty("expires_in")
    Long expiresIn;

    @Schema(description = "Refresh token JWT (larga duración) para renovar el access token", example = "eyJhbGc...")
    @JsonProperty("refresh_token")
    String refreshToken;

    @Schema(description = "Scope asociado al token (si aplica)", example = "read write")
    @JsonProperty("scope")
    String scope;
}
