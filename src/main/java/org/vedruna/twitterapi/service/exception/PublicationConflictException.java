package org.vedruna.twitterapi.service.exception;

/**
 * Se lanza cuando ocurre un conflicto al manejar una publicación
 * (por ejemplo, intentar editar o eliminar una publicación que no pertenece al usuario).
 */
public class PublicationConflictException extends RuntimeException {

    public PublicationConflictException() {
        super("Publication conflict occurred");
    }

    public PublicationConflictException(String message) {
        super(message);
    }
}
