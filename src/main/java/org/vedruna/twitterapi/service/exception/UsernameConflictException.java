package org.vedruna.twitterapi.service.exception;

/**
 * Lanzada cuando ya existe un username en la base de datos.
 */
public class UsernameConflictException extends RuntimeException {
    public UsernameConflictException() {
        super("Username already exists");
    }
    public UsernameConflictException(String message) {
        super(message);
    }
}
