package org.vedruna.twitterapi.persistance.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad Users (tabla users).
 */
@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    // CHAR(60) segun tu DDL
    @Column(name = "password", nullable = false, columnDefinition = "CHAR(60)")
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 90)
    private String email;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    // Relación con roles
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;

    // Publicaciones del usuario
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PublicationEntity> publications;

    /**
     * Usuarios a los que este usuario sigue.
     * Join table: users_follow_users(user_who_follows_id, user_to_follow_id)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_follow_users",
        joinColumns = @JoinColumn(name = "user_who_follows_id"),
        inverseJoinColumns = @JoinColumn(name = "user_to_follow_id")
    )
    private Set<UserEntity> following;

    /**
     * Usuarios que siguen a este usuario.
     * Inverso de following.
     */
    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<UserEntity> followers;
}
