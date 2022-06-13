package org.optaweb.vehiclerouting.service.error;

/**
 * Consumes error messages.
 */
public interface ErrorMessageConsumer {

    /**
     * Consume an error message.
     *
     * @param message error message
     */
    void consumeMessage(ErrorMessage message);
}
