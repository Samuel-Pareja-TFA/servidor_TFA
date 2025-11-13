package org.vedruna.twitterapi.persistance.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entidad que representa un usuario del sistema (tabla {@code users}).
 *
 * <p>Esta clase modela el usuario persistido en la base de datos y además
 * implementa {@link UserDetails} para integrarse con Spring Security.
 * Contiene información de identificación, credenciales, perfil y
 * relaciones con otras entidades (roles, publicaciones y relaciones de
 * seguimiento entre usuarios).</p>
 *
 * <p>Anotaciones principales:</p>
 * <ul>
 *   <li>{@code @Entity}: indica que la clase es una entidad JPA persistible.</li>
 *   <li>{@code @Table(name = "users")}: mapea la clase a la tabla {@code users}.
 *   </li>
 *   <li>{@code @Data} (Lombok): genera automáticamente getters/setters,
 *       {@code equals}, {@code hashCode} y {@code toString}. Ten en cuenta
 *       que incluir relaciones en {@code toString} puede provocar cargas
 *       perezosas o recursividad en relaciones bidireccionales.</li>
 * </ul>
 *
 * <p>Responsabilidades y comportamientos:</p>
 * <ul>
 *   <li>Actuar como DTO/POJO de persistencia: no contiene lógica de negocio
 *       compleja; las operaciones se implementan en servicios.</li>
 *   <li>Proveer información para Spring Security a través de
 *       {@code UserDetails} (username, password, authorities y estados).</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Identificador único del usuario (clave primaria).
     *
     * <p>Anotaciones:</p>
     * <ul>
     *   <li>{@code @Id}: marca la clave primaria.</li>
     *   <li>{@code @GeneratedValue(strategy = GenerationType.IDENTITY)}: valor
     *       autogenerado por la base de datos (auto-increment).</li>
     *   <li>{@code @Column(name = "id", nullable = false)}: mapea la columna
     *       y exige not-null en esquema.</li>
     * </ul>
     */

    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    /**
     * Nombre de usuario (login) que identifica de forma única al usuario en
     * el sistema. Debe ser único y no nulo.
     */

    // CHAR(60) segun tu DDL
    @Column(name = "password", nullable = false, columnDefinition = "CHAR(60)")
    private String password;

    /**
     * Contraseña cifrada (hash). Se almacena como CHAR(60) según el DDL del
     * proyecto (ej. bcrypt produce hashes de longitud adecuada). Nunca debe
     * almacenarse la contraseña en claro.
     */

    @Column(name = "email", nullable = false, unique = true, length = 90)
    private String email;

    /**
     * Correo electrónico del usuario. Un valor obligatorio y único por
     * usuario en este modelo.
     */

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    /**
     * Descripción o biografía del usuario. Campo opcional y de texto largo
     * (LONGTEXT) para permitir contenidos extensos.
     */

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    /**
     * Fecha de creación del usuario (solo fecha). Debe establecerse al crear
     * la cuenta (por ejemplo, en la capa de servicio). Representar siempre
     * con la política de zona horaria acordada por la aplicación.
     */

    // Relación con roles
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;

    /**
     * Rol asociado al usuario (relación Many-to-One).
     *
     * <p>Anotaciones:</p>
     * <ul>
     *   <li>{@code @ManyToOne(fetch = FetchType.EAGER)}: cada usuario tiene un
     *       rol; se carga eager porque las autoridades suelen necesitarse en
     *       autenticación/autorización.</li>
     *   <li>{@code @JoinColumn(name = "role_id", referencedColumnName = "id")}:
     *       columna foránea que referencia {@code roles.id}.</li>
     * </ul>
     */

    // Publicaciones del usuario
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PublicationEntity> publications;

    /**
     * Publicaciones creadas por el usuario (relación One-to-Many).
     *
     * <p>Se usa {@code LAZY} para cargar las publicaciones solo cuando se
     * solicitan. {@code CascadeType.ALL} indica que las operaciones de
     * persistencia sobre el usuario (p. ej. eliminar) se propaguen a las
     * publicaciones; comprueba que este comportamiento es el deseado.
     */

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
     * Conjunto de usuarios que este usuario sigue.
     *
     * <p>Mapa la tabla intermedia {@code users_follow_users} con las columnas
     * {@code user_who_follows_id} y {@code user_to_follow_id}. Se usa
     * {@code LAZY} para evitar cargas no deseadas.</p>
     */

    /**
     * Usuarios que siguen a este usuario.
     * Inverso de following.
     */
    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<UserEntity> followers;

    /**
     * Conjunto de usuarios que siguen a este usuario (lado inverso de
     * {@link #following}).</p>
     */


     /* -----------------------------
       Métodos de UserDetails
       ----------------------------- */

    /**
     * Devuelve las authorities (permisos) que tiene el usuario a partir del
     * rol asociado.
     *
     * <p>Lógica:</p>
     * <ol>
     *   <li>Si no existe rol o el nombre del rol es {@code null}, devuelve una
     *       colección vacía.</li>
     *   <li>Construye una autoridad con el formato {@code ROLE_<ROL>} en
     *       mayúsculas y la envuelve en {@link SimpleGrantedAuthority}.</li>
     * </ol>
     *
     * @return colección de {@link GrantedAuthority} representando las
     *         authorities del usuario (nunca {@code null}, puede estar vacía).
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
     * Devuelve el nombre de usuario usado por Spring Security.
     *
     * @return nombre de usuario único (login).
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Devuelve la contraseña (hash) para la autenticación con Spring
     * Security.
     *
     * @return contraseña cifrada / hash.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Indica si la cuenta no ha expirado.
     *
     * <p>Esta implementación devuelve siempre {@code true}. Si deseas
     * controlar expiraciones deberías persistir un campo con la fecha de
     * expiración o cambiar la lógica aquí.</p>
     *
     * @return {@code true} si la cuenta no ha expirado.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta no está bloqueada.
     *
     * @return {@code true} si la cuenta no está bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales (por ejemplo, la contraseña) no han
     * expirado.
     *
     * @return {@code true} si las credenciales siguen siendo válidas.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado (activo) en el sistema.
     *
     * @return {@code true} si el usuario está activo y puede autenticarse.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
