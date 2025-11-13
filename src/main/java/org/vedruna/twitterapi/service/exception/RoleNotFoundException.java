package org.vedruna.twitterapi.service.exception;

/**
 * Excepción que indica que no se ha encontrado un rol solicitado en el sistema.
 *
 * <p>Semántica: se utiliza cuando una operación depende de la existencia de un rol
 * (por ejemplo al asignar un rol por defecto al crear un usuario) y el rol no existe
 * en la persistencia. En la capa REST esta situación suele mapearse a un 404 (Not Found).
 *
 * <p>Notas de uso:
 * <ul>
 *   <li>Es una excepción unchecked ({@link RuntimeException}) para simplificar su
 *       lanzamiento desde los servicios sin forzar el manejo en todas las firmas.</li>
 *   <li>Para respuestas API más ricas, el handler global puede convertir esta excepción
 *       en un payload estructurado (código, mensaje, metadatos) y en un estado HTTP 404.
 *   <li>No incluir información sensible en los mensajes de error.</li>
 * </ul>
 */
public class RoleNotFoundException extends RuntimeException {

    /**
     * Constructor por defecto con mensaje estándar.
     */
    public RoleNotFoundException() {
        super("Role not found");
    }

    /**
     * Constructor con mensaje personalizado para añadir contexto (por ejemplo el nombre del rol buscado).
     *
     * @param message mensaje descriptivo de la excepción.
     */
    public RoleNotFoundException(String message) {
        super(message);
    }
}
