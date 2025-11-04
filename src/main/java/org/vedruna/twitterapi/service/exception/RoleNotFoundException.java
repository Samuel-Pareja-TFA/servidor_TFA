package org.vedruna.twitterapi.service.exception;

/**
 * Se lanza cuando no se encuentra un rol solicitado.
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException() {
        super("Role not found");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
