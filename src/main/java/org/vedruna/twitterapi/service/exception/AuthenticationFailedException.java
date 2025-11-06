package org.vedruna.twitterapi.service.exception;

/**
 * Lanzada cuando las credenciales no son válidas (login fallido).
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException() {
        super("Authentication failed: invalid credentials");
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
