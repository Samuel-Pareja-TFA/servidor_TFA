package org.vedruna.twitterapi.service.exception;

/**
 * Lanzada cuando ya existe un email en la base de datos.
 */
public class EmailConflictException extends RuntimeException {
    public EmailConflictException() {
        super("Email already exists");
    }
    public EmailConflictException(String message) {
        super(message);
    }
}
