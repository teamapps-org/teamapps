package org.teamapps.ux.component.field.textcolormarker;

public class IllegalTextColorMarkerException extends RuntimeException {

    public IllegalTextColorMarkerException(String message) {
        super(message);
    }

    public IllegalTextColorMarkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTextColorMarkerException(Throwable cause) {
        super(cause);
    }

    public IllegalTextColorMarkerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
