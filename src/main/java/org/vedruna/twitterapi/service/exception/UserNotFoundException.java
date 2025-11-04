package org.vedruna.twitterapi.service.exception;

import java.util.NoSuchElementException;

/**
 * Excepción personalizada para indicar que un usuario no existe.
 * Extiende {@link NoSuchElementException} (RuntimeException) para no
 * forzar el uso en las firmas de método.
 */
public class UserNotFoundException extends NoSuchElementException {

    /**
     * Constructor por defecto con mensaje por defecto.
     */
    public UserNotFoundException() {
        super("User not found");
    }

    /**
     * Constructor con mensaje personalizado.
     * @param message Mensaje de la excepción.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
