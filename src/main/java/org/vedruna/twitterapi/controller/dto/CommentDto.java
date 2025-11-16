package org.vedruna.twitterapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Integer id;
    private String text;
    private LocalDateTime createDate;

    private Integer userId;
    private String username;

    private Integer publicationId;
}
