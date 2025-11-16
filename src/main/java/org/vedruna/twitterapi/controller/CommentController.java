package org.vedruna.twitterapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vedruna.twitterapi.controller.dto.CommentDto;
import org.vedruna.twitterapi.controller.dto.CreateCommentDto;
import org.vedruna.twitterapi.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{publicationId}/user/{userId}")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Integer publicationId,
            @PathVariable Integer userId,
            @RequestBody CreateCommentDto dto
    ) {
        return ResponseEntity.ok(commentService.addComment(publicationId, userId, dto));
    }

    @GetMapping("/{publicationId}")
    public ResponseEntity<List<CommentDto>> listComments(@PathVariable Integer publicationId) {
        return ResponseEntity.ok(commentService.getCommentsByPublication(publicationId));
    }
}
