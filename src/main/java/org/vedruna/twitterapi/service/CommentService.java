package org.vedruna.twitterapi.service;

import org.vedruna.twitterapi.controller.dto.CommentDto;
import org.vedruna.twitterapi.controller.dto.CreateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Integer publicationId, Integer userId, CreateCommentDto dto);

    List<CommentDto> getCommentsByPublication(Integer publicationId);
}
