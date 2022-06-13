package org.optaweb.vehiclerouting.service.error;

import java.util.Objects;

public class ErrorMessage {

    /**
     * Message ID (never {@code null}).
     */
    public final String id;
    /**
     * Message text (never {@code null}).
     */
    public final String text;

    public static ErrorMessage of(String id, String text) {
        return new ErrorMessage(id, text);
    }

    private ErrorMessage(String id, String text) {
        this.id = Objects.requireNonNull(id);
        this.text = Objects.requireNonNull(text);
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
