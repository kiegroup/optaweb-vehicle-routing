package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.Objects;

import org.optaweb.vehiclerouting.service.error.ErrorMessage;

/**
 * Portable error message.
 */
public class PortableErrorMessage {

    private final String id;
    private final String text;

    public static PortableErrorMessage fromMessage(ErrorMessage message) {
        return new PortableErrorMessage(message.id, message.text);
    }

    PortableErrorMessage(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PortableErrorMessage that = (PortableErrorMessage) o;
        return id.equals(that.id) &&
                text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }

    @Override
    public String toString() {
        return "PortableErrorMessage{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
