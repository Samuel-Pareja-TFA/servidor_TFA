package org.vedruna.twitterapi.service.exception;

import java.util.NoSuchElementException;

/**
 * Excepción personalizada para indicar que una publicación no existe.
 * Extiende {@link NoSuchElementException}.
 */
public class PublicationNotFoundException extends NoSuchElementException {

    /**
     * Constructor por defecto con mensaje por defecto.
     */
    public PublicationNotFoundException() {
        super("Publication not found");
    }

    /**
     * Constructor con mensaje personalizado.
     * @param message Mensaje de la excepción.
     */
    public PublicationNotFoundException(String message) {
        super(message);
    }
}
