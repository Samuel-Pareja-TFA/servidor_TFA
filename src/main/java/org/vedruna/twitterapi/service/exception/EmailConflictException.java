package org.vedruna.twitterapi.service.exception;

/**
 * Excepción de dominio que indica que ya existe un usuario con el mismo email en
 * la base de datos.
 *
 * <p>Semántica:
 * <ul>
 *   <li>Representa un conflicto de integridad de datos: el email debe ser único
 *       y ya existe uno registrado.</li>
 *   <li>En la capa web se recomienda mapear esta excepción a un
 *       <code>409 Conflict</code> para que el cliente entienda que el recurso
 *       no se puede crear por duplicidad.</li>
 * </ul>
 *
 * <p>Ejemplos de uso:
 * <ul>
 *   <li>Intentar registrar un usuario con un email que ya está en la base de datos.</li>
 *   <li>Actualizar un email de usuario a uno que ya pertenece a otro usuario.</li>
 * </ul>
 *
 * <p>Consideraciones de diseño:
 * <ul>
 *   <li>Es unchecked (hereda de {@link RuntimeException}), se propaga hasta
 *       el controlador sin obligar a capturarla en capas intermedias.</li>
 *   <li>Si se desea información adicional, se puede enriquecer la respuesta
 *       desde un {@code @ControllerAdvice} global.</li>
 * </ul>
 *
 * <p>Hilos y seguridad: la clase es inmutable y segura para ser lanzada desde
 * múltiples hilos simultáneamente.</p>
 */
public class EmailConflictException extends RuntimeException {

    /**
     * Constructor por defecto con mensaje genérico en inglés.
     */
    public EmailConflictException() {
        super("Email already exists");
    }

    /**
     * Constructor que acepta un mensaje personalizado.
     *
     * @param message mensaje descriptivo del conflicto
     */
    public EmailConflictException(String message) {
        super(message);
    }
}
