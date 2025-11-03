package org.vedruna.twitterapi.persistance.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad Roles (tabla roles).
 */
@Data
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<UserEntity> users;
}
