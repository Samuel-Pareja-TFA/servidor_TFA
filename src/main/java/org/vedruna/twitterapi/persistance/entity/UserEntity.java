package org.vedruna.twitterapi.persistance.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entidad Users (tabla users).
 */
@Data
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails{

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
    @ManyToOne(fetch = FetchType.EAGER)
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


     /* -----------------------------
       Métodos de UserDetails
       ----------------------------- */

    /**
     * Devuelve las authorities/granted authorities a partir del role asociado.
     * Si no hay role, devuelve una colección vacía.
     *
     * Por convención añadimos el prefijo "ROLE_" y lo ponemos en mayúsculas.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null || this.role.getName() == null) {
            return Collections.emptyList();
        }
        String authority = "ROLE_" + this.role.getName().toUpperCase();
        return List.of(new SimpleGrantedAuthority(authority));
    }

    /**
     * Username para Spring Security (ya existía el campo username).
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Password para Spring Security (ya existía el campo password).
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Indica si la cuenta no ha expirado.
     * En esta implementación siempre true (como en el ejemplo del profesor).
     * Puedes cambiar la lógica si necesitas controlar expiraciones.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta no está bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales no han expirado.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
