package org.vedruna.twitterapi.persistance.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Representa un rol del sistema persistido en la base de datos.
 *
 * <p>Esta entidad se mapea a la tabla {@code roles}. Un rol es una unidad de
 * autorización (por ejemplo: {@code ROLE_USER}, {@code ROLE_ADMIN}) asociada a
 * usuarios. La clase está pensada para ser un simple contenedor de estado
 * (POJO) y no debe contener lógica compleja de negocio.</p>
 *
 * <p>Notas importantes y consideraciones:</p>
 * <ul>
 *   <li>{@code @Entity}: marca la clase como una entidad JPA; el proveedor
 *       JPA la gestionará para persistencia/lectura.</li>
 *   <li>{@code @Table(name = "roles")}: especifica el nombre de la tabla
 *       en la base de datos. Es buena práctica dejar explícito el nombre
 *       para evitar dependencias de convenciones.</li>
 *   <li>{@code @Data} (Lombok): genera automáticamente getters, setters,
 *       {@code equals}, {@code hashCode} y {@code toString}. Esto reduce el
 *       código repetitivo, pero hay dos advertencias:
 *       <ul>
 *         <li>En relaciones bidireccionales (como la colección {@code users})
 *             hay que evitar que {@code toString}, {@code equals} o
 *             {@code hashCode} naveguen la colección completa: puede
 *             provocar cargas perezosas inesperadas (LazyInitialization)
 *             o recursividad. Considera usar {@code @ToString.Exclude} o
 *             {@code @EqualsAndHashCode.Exclude} si detectas problemas.</li>
 *         <li>Al implementar {@code equals/hashCode} con Lombok por defecto,
 *             la inclusión de colecciones puede afectar el comportamiento en
 *             contextos de persistencia; normalmente es mejor basar
 *             {@code equals/hashCode} en el identificador cuando sea seguro.</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p>Esta clase no define métodos explícitos más allá de los generados por
 * Lombok. Los puntos donde añadir logs o validaciones son los servicios
 * o controladores que la manipulan, no la entidad en sí.</p>
 */
@Data
@Entity
@Table(name = "roles")
public class RoleEntity {

    /**
     * Identificador único de la entidad (clave primaria).
     *
     * <p>Anotaciones:</p>
     * <ul>
     *   <li>{@code @Id}: indica que este campo es la clave primaria.</li>
     *   <li>{@code @GeneratedValue(strategy = GenerationType.IDENTITY)}: la BD
     *       genera el valor (auto-increment). Esto implica que el campo
     *       estará {@code null} hasta que la entidad se persista.</li>
     *   <li>{@code @Column(name = "id", nullable = false)}: mapea la columna
     *       {@code id} y especifica que no puede ser nula.</li>
     * </ul>
     *
     * Tipo y comportamiento: se usa {@code Integer} (objeto) para permitir
     * {@code null} mientras la entidad no se haya persistido; tras persistir
     * tendrá un valor entero asignado por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Nombre del rol.
     *
     * <p>Esta columna almacena una cadena que representa la autoridad del rol,
     * por ejemplo {@code ROLE_USER} o {@code ROLE_ADMIN}.</p>
     *
     * <p>Anotaciones:</p>
     * <ul>
     *   <li>{@code @Column(name = "name", nullable = false, unique = true, length = 50)}:
     *       mapea la columna {@code name} con las restricciones:
     *       <ul>
     *         <li>{@code nullable = false}: valor obligatorio.</li>
     *         <li>{@code unique = true}: debe ser único en la tabla.</li>
     *         <li>{@code length = 50}: longitud máxima de la columna (VARCHAR).</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * Reglas de negocio típicas: el nombre debe seguir la convención de
     * prefijos (por ejemplo, {@code ROLE_}) si la aplicación lo requiere.
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Usuarios asociados a este rol.
     *
     * <p>Relación bidireccional One-to-Many: un rol puede estar asignado a
     * múltiples usuarios. En la mayoría de los modelos la entidad propietaria
     * de la relación es {@code UserEntity} (campo {@code role}), por eso aquí
     * usamos {@code mappedBy} para indicar que esta es la vista inversa.</p>
     *
     * <p>Anotaciones y significado:</p>
     * <ul>
     *   <li>{@code @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)}:
     *       indica la cardinalidad y la propiedad propietaria de la relación.
     *       {@code FetchType.LAZY} indica carga perezosa de la colección (no
     *       se cargan los usuarios a menos que se solicite explícitamente).</li>
     * </ul>
     *
     * Consideraciones de uso:
     * <ul>
     *   <li>Evitar navegar esta colección dentro de métodos que se ejecuten
     *       fuera del contexto de persistencia (p. ej. después de cerrar una
     *       transacción), para no provocar {@code LazyInitializationException}.
     *   </li>
     *   <li>Si necesitas la lista de usuarios con frecuencia, valora usar
     *       consultas específicas (JPQL/Criteria) o cambiar a {@code EAGER}
     *       con precaución (puede causar problemas de rendimiento).</li>
     * </ul>
     */
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<UserEntity> users;
}
