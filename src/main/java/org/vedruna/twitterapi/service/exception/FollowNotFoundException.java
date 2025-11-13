package org.vedruna.twitterapi.service.exception;


/**
 * Excepción que indica que no existe una relación de seguimiento (follow) entre dos
 * usuarios.
 *
 * <p>Semántica:
 * <ul>
 *   <li>Representa un recurso no encontrado desde la perspectiva del dominio: la
 *       entidad/relación "follow" solicitada no existe.</li>
 *   <li>En la capa HTTP se recomienda mapear esta excepción a un <code>404 Not Found</code>
 *       para que el cliente entienda que el recurso buscado no existe.</li>
 * </ul>
 *
 * <p>Ejemplos de cuándo lanzarla:
 * <ul>
 *   <li>Se solicita dejar de seguir a un usuario y no existe la relación de seguimiento.</li>
 *   <li>Se intenta consultar los detalles de una relación follow por id y no se encuentra.</li>
 * </ul>
 *
 * <p>Consideraciones de diseño:
 * <ul>
 *   <li>Es unchecked (hereda de {@link RuntimeException}) para permitir que se propague
 *       hasta un manejador global de excepciones sin forzar su captura en capas intermedias.</li>
 *   <li>Si se necesita devolver información adicional al cliente (por ejemplo, ids
 *       solicitados, razones o códigos de error internos), considerar enriquecer la
 *       respuesta desde el {@code @ControllerAdvice} que atrape esta excepción.</li>
 * </ul>
 *
 * <p>Hilos y seguridad: la clase no contiene estado mutable y es segura para lanzar
 * desde cualquier hilo.</p>
 */
public class FollowNotFoundException extends RuntimeException {

    /**
     * Constructor por defecto con mensaje en inglés estándar.
     */
    public FollowNotFoundException() {
        super("Follow relationship not found");
    }

    /**
     * Constructor con mensaje personalizado para proporcionar contexto adicional en logs
     * o en la respuesta HTTP si el manejador lo incluye.
     *
     * @param message mensaje descriptivo del error
     */
    public FollowNotFoundException(String message) {
        super(message);
    }
}
