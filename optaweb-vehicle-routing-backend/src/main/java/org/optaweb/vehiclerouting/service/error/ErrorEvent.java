package org.optaweb.vehiclerouting.service.error;

import java.util.Objects;

public class ErrorEvent {

    public final String message;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *        which the event is associated (never {@code null})
     * @param message error message (never {@code null})
     */
    public ErrorEvent(Object source, String message) {
        this.message = Objects.requireNonNull(message);
    }
}
