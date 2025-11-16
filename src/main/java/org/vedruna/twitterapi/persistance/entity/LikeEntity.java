package org.vedruna.twitterapi.persistance.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa un "like" realizado por un usuario sobre una publicación.
 *
 * <p>Tabla asociada: {@code publication_likes}.</p>
 *
 * <p>Cada registro indica que un usuario concreto (user_id) ha hecho "me gusta"
 * sobre una publicación concreta (publication_id) en una fecha/hora determinada.</p>
 *
 * <p>Restricciones importantes a nivel de BD:</p>
 * <ul>
 *   <li>Existe un índice único (user_id, publication_id) para evitar likes duplicados.</li>
 *   <li>Existen claves foráneas a {@code users.id} y {@code publications.id} con
 *       borrado en cascada, de forma que si se borra un usuario o publicación,
 *       sus likes asociados desaparecen automáticamente.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "publication_likes")
public class LikeEntity {

    /**
     * Identificador interno del like.
     *
     * <p>Clave primaria auto-incremental. No se expone normalmente al cliente,
     * ya que lo relevante a nivel de negocio suele ser (user, publication).</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Usuario que realiza el like.
     *
     * <p>Relación ManyToOne hacia {@link UserEntity}. Cada like pertenece a
     * un único usuario, pero un usuario puede tener muchos likes.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Publicación sobre la que se realiza el like.
     *
     * <p>Relación ManyToOne hacia {@link PublicationEntity}. Cada like
     * pertenece a una única publicación, pero una publicación puede tener
     * muchos likes.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publication_id", nullable = false)
    private PublicationEntity publication;

    /**
     * Fecha y hora en la que se realizó el like.
     *
     * <p>Se establece normalmente en la capa de servicio al momento de
     * crear el like.</p>
     */
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
}
