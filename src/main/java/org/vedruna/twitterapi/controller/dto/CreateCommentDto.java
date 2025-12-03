package org.vedruna.twitterapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateCommentDto {

    @Schema(description = "Texto del comentario", example = "Buen post!")
    private String text;
}
