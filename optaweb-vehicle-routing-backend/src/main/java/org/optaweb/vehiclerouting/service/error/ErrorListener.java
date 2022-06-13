package org.optaweb.vehiclerouting.service.error;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Creates messages from error events and passes them to consumers.
 */
@ApplicationScoped
public class ErrorListener {

    private final Event<ErrorMessage> errorMessageEvent;

    @Inject
    public ErrorListener(Event<ErrorMessage> errorMessageEvent) {
        this.errorMessageEvent = errorMessageEvent;
    }

    public void onErrorEvent(@Observes ErrorEvent event) {
        errorMessageEvent.fire(ErrorMessage.of(UUID.randomUUID().toString(), event.message));
    }
}
