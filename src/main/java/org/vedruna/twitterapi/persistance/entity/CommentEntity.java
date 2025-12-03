package org.vedruna.twitterapi.persistance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa un comentario en una publicación.
 */
@Data
@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Texto del comentario.
     */
    @Column(name = "text", nullable = false, columnDefinition = "LONGTEXT")
    private String text;

    /**
     * Fecha de creación del comentario.
     */
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    /**
     * Usuario autor del comentario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * Publicación comentada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id")
    private PublicationEntity publication;
}
