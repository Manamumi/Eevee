package xyz.eevee.eevee.exc;

import lombok.Getter;

public class EeveeActionException extends RuntimeException {
    @Getter
    private Throwable cause;

    public EeveeActionException(String message) {
        super(message);
    }

    public EeveeActionException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
}
