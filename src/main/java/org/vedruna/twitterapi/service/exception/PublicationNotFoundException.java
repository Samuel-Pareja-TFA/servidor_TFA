package org.vedruna.twitterapi.service.exception;

import java.util.NoSuchElementException;

/**
 * Excepción de dominio que indica que una publicación no existe en el sistema.
 *
 * <p>Se extiende {@link NoSuchElementException} (unchecked) para permitir su
 * lanzamiento desde los servicios sin obligar a su declaración en las firmas.
 * En la capa REST se recomienda mapear esta excepción a un HTTP 404 (Not Found).
 *
 * <p>Ejemplo de uso:
 * <pre>
 * publicationRepository.findById(id)
 *     .orElseThrow(() -> new PublicationNotFoundException(id.toString()));
 * </pre>
 *
 * Consideraciones:
 * <ul>
 *   <li>Si se necesita más contexto en la respuesta HTTP (código de error, metadatos),
 *       implementar un handler global que transforme la excepción en un payload estructurado.</li>
 *   <li>Evitar incluir información sensible en el mensaje de la excepción.</li>
 * </ul>
 */
public class PublicationNotFoundException extends NoSuchElementException {

    /**
     * Constructor por defecto con mensaje estándar.
     */
    public PublicationNotFoundException() {
        super("Publication not found");
    }

    /**
     * Constructor con mensaje personalizado para añadir contexto (por ejemplo el id de la publicación).
     *
     * @param message mensaje descriptivo de la excepción.
     */
    public PublicationNotFoundException(String message) {
        super(message);
    }
}
