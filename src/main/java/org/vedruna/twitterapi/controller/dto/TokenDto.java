package org.vedruna.twitterapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para devolver un token (temporal).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Token DTO (temporal)")
public class TokenDto {
    @Schema(description = "Token de autenticación (temporal)", example = "DUMMY-TOKEN-123")
    private String token;
}
