package org.vedruna.twitterapi.service.exception;

/**
 * Excepción de dominio que representa un conflicto al realizar operaciones sobre una
 * publicación (por ejemplo: editar o eliminar una publicación que no pertenece al
 * usuario autenticado, o intentar crear/actualizar una publicación en un estado
 * incompatible).
 *
 * <p>Semántica:
 * <ul>
 *   <li>Esta excepción debe usarse para indicar que la operación solicitada es válida
 *       sintácticamente pero entra en conflicto con el estado actual del recurso.</li>
 *   <li>En la capa web (controladores), se recomienda mapear esta excepción a un
 *       HTTP 409 Conflict mediante un {@code @ControllerAdvice} o un manejador global
 *       de excepciones para devolver un mensaje claro al cliente.</li>
 * </ul>
 *
 * <p>Ejemplos de uso:
 * <ul>
 *   <li>Un usuario intenta eliminar una publicación que pertenece a otro usuario.</li>
 *   <li>Se intenta aplicar una operación de edición que viola reglas de negocio
 *       (p. ej. cambiar el autor o el identificador asociado de forma no permitida).</li>
 * </ul>
 *
 * <p>Consideraciones de diseño:
 * <ul>
 *   <li>No contiene estado adicional — es una subclase de {@link RuntimeException} —
 *       por lo que su manejo es de tipo unchecked y puede propagarse hasta la capa
 *       de presentación.</li>
 *   <li>Si la aplicación requiere más detalles para el cliente (códigos internos,
 *       campos en conflicto, metadata), considerar crear una excepción con campos
 *       adicionales o enriquecer la respuesta desde el controlador que captura esta
 *       excepción.</li>
 * </ul>
 *
 * <p>Hilos y seguridad: esta clase es inmutable (no almacena campos mutables) y por
 * tanto es segura para ser lanzada desde múltiples hilos.</p>
 */
public class PublicationConflictException extends RuntimeException {

    /**
     * Constructor por defecto con un mensaje genérico en inglés para compatibilidad con
     * mensajes existentes. Se recomienda usar el constructor con mensaje personalizado
     * cuando haya que dar contexto adicional al cliente o al log.
     */
    public PublicationConflictException() {
        super("Publication conflict occurred");
    }

    /**
     * Constructor que acepta un mensaje personalizado.
     *
     * @param message mensaje descriptivo del conflicto (visible en logs y, opcionalmente,
     *                en la respuesta HTTP si el manejador de excepciones lo incluye).
     */
    public PublicationConflictException(String message) {
        super(message);
    }
}
