package org.vedruna.twitterapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vedruna.twitterapi.controller.dto.CommentDto;
import org.vedruna.twitterapi.controller.dto.CreateCommentDto;
import org.vedruna.twitterapi.persistance.entity.CommentEntity;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.CommentRepository;
import org.vedruna.twitterapi.persistance.repository.PublicationRepository;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.CommentService;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final PublicationRepository publicationRepo;
    private final UserRepository userRepo;

    @Override
    public CommentDto addComment(Integer publicationId, Integer userId, CreateCommentDto dto) {

        PublicationEntity pub = publicationRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        CommentEntity c = new CommentEntity();
        c.setText(dto.getText());
        c.setCreateDate(LocalDateTime.now());
        c.setPublication(pub);
        c.setUser(user);

        CommentEntity saved = commentRepo.save(c);

        return toDto(saved);
    }

    @Override
    public List<CommentDto> getCommentsByPublication(Integer publicationId) {
        return commentRepo.findByPublicationId(publicationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private CommentDto toDto(CommentEntity c) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setText(c.getText());
        dto.setCreateDate(c.getCreateDate());
        dto.setUserId(c.getUser().getId());
        dto.setUsername(c.getUser().getUsername());
        dto.setPublicationId(c.getPublication().getId());
        return dto;
    }
}
