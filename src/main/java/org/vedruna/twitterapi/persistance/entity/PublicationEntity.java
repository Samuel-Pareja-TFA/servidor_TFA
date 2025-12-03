package org.vedruna.twitterapi.persistance.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa una publicación realizada por un usuario.
 *
 * <p>Se mapea a la tabla {@code publications}. Cada instancia contiene la
 * referencia al usuario autor, el texto de la publicación y marcas de tiempo
 * de creación y actualización. Esta entidad es un POJO gestionado por JPA y
 * no debe contener lógica de negocio compleja; las operaciones (crear,
 * actualizar, eliminar) deben implementarse en servicios.</p>
 *
 * <p>Anotaciones principales:</p>
 * <ul>
 *   <li>{@code @Entity}: marca la clase como entidad JPA para persistencia.</li>
 *   <li>{@code @Table(name = "publications")}: define el nombre de la tabla
 *       en la base de datos. Mantener el nombre explícito evita depender de
 *       convenciones del proveedor.</li>
 *   <li>{@code @Data} (Lombok): genera getters, setters, {@code equals},
 *       {@code hashCode} y {@code toString}. Ten en cuenta las mismas
 *       consideraciones que en otras entidades: evitar incluir colecciones u
 *       relaciones perezosas en {@code toString} o {@code equals} para no
 *       provocar cargas no deseadas.</li>
 * </ul>
 *
 * <p>Campos principales y su significado:</p>
 * <ul>
 *   <li>{@link #id}: identificador primario de la publicación. Generado por la
 *       base de datos (IDENTITY), estará {@code null} hasta la persistencia.</li>
 *   <li>{@link #user}: referencia al autor (usuario) de la publicación. Es una
 *       relación Many-to-One; la columna de la BD es {@code user_id} y se
 *       configura con carga {@code LAZY} para evitar traer el usuario salvo
 *       que sea necesario.</li>
 *   <li>{@link #text}: contenido textual de la publicación. Está mapeado con
 *       {@code columnDefinition = "LONGTEXT"} para admitir textos largos.
 *       Se marca {@code nullable = false} porque una publicación sin texto no
 *       tiene sentido en este modelo.</li>
 *   <li>{@link #createDate}: fecha/hora de creación. Es obligatoria y debe
 *       representarse en UTC o con la política de zona horaria acordada por la
 *       aplicación.</li>
 *   <li>{@link #updateDate}: fecha/hora de la última actualización. Es opcional
 *       ({@code null} si nunca se ha editado) y se actualiza por la lógica de
 *       negocio al modificar la publicación.</li>
 * </ul>
 *
 * <p>Consideraciones de uso y buenas prácticas:</p>
 * <ol>
 *   <li>La relación {@code user} es {@code LAZY}; evita acceder a
 *       {@code publication.getUser()} fuera de una transacción abierta para
 *       no provocar {@code LazyInitializationException}.</li>
 *   <li>Si necesitas mostrar la publicación junto al autor en una consulta,
 *       prefiere realizar una consulta con {@code JOIN FETCH} o una proyección
 *       para evitar múltiples accesos a la BD (problema N+1).</li>
 *   <li>El campo {@code text} usa una definición larga; revisa la base de
 *       datos para asegurar que el tipo exacto (LONGTEXT) está soportado.
 *       Alternativamente, usa {@code @Lob} si prefieres una abstracción JPA.
 *   </li>
 *   <li>La gestión de las marcas de tiempo ({@code createDate}/{@code updateDate})
 *       puede automatizarse con listeners JPA (@PrePersist/@PreUpdate) o con
 *       la lógica en el servicio; decide un enfoque coherente y documentado.</li>
 * </ol>
 */
@Data
@Entity
@Table(name = "publications")
public class PublicationEntity {

    /**
     * Identificador único de la publicación (clave primaria).
     *
     * <p>Anotaciones:</p>
     * <ul>
     *   <li>{@code @Id}: marca la propiedad como clave primaria.</li>
     *   <li>{@code @GeneratedValue(strategy = GenerationType.IDENTITY)}: el valor
     *       lo genera la base de datos (auto-increment).</li>
     *   <li>{@code @Column(name = "id", nullable = false)}: mapea la columna
     *       y exige not-null a nivel de esquema.</li>
     * </ul>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Autor de la publicación (relación Many-to-One).
     *
     * <p>Anotaciones:</p>
     * <ul>
     *   <li>{@code @ManyToOne(fetch = FetchType.LAZY)}: indica que muchas
     *       publicaciones pueden pertenecer a un mismo usuario. {@code LAZY}
     *       evita cargar el usuario automáticamente junto con la publicación.</li>
     *   <li>{@code @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)}:
     *       mapea la columna foránea {@code user_id} que referencia a
     *       {@code users.id}; {@code nullable = false} fuerza que toda
     *       publicación tenga un autor asociado.</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    /**
     * Contenido textual de la publicación.
     *
     * <p>Se usa {@code columnDefinition = "LONGTEXT"} para permitir textos
     * extensos; dependiendo del dialecto de BD puede equivaler a CLOB/LONGTEXT.
     * Es obligatorio ({@code nullable = false}).</p>
     */
    @Column(name = "text", nullable = false, columnDefinition = "LONGTEXT")
    private String text;

    /**
     * Fecha y hora de creación de la publicación.
     *
     * <p>Debe asignarse al crear el registro (por ejemplo, en el servicio o
     * con un listener {@code @PrePersist}). Representar siempre en la misma
     * zona horaria (p. ej. UTC) o documentar la política usada.</p>
     */
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    /**
     * Fecha y hora de la última actualización. Puede ser {@code null} si
     * nunca se ha editado la publicación. Actualizarse en {@code @PreUpdate}
     * o desde la capa de servicio cuando se modifique el texto u otros
     * atributos relevantes.
     */
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
