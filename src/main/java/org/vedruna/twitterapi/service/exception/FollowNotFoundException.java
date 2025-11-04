package org.vedruna.twitterapi.service.exception;


/**
 * Se lanza cuando no se encuentra una relación de seguimiento entre usuarios.
 */
public class FollowNotFoundException extends RuntimeException {

    public FollowNotFoundException() {
        super("Follow relationship not found");
    }

    public FollowNotFoundException(String message) {
        super(message);
    }
}
