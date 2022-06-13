package org.optaweb.vehiclerouting.plugin.rest.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.service.error.ErrorMessage;
import org.optaweb.vehiclerouting.util.jackson.JacksonAssertions;
import org.optaweb.vehiclerouting.util.junit.FileContent;

class PortableErrorMessageTest {

    @Test
    void marshal_to_json(@FileContent("portable-error-message.json") String expectedJson) {
        String id = "c670dd37-62fb-4e86-95ed-c1f4953aaeaa";
        String text = "Error message.\nDetails.";
        PortableErrorMessage portableErrorMessage = PortableErrorMessage.fromMessage(ErrorMessage.of(id, text));
        JacksonAssertions.assertThat(portableErrorMessage).serializedIsEqualToJson(expectedJson);
    }

    @Test
    void factory_method() {
        String id = "id";
        String text = "error";
        PortableErrorMessage portableErrorMessage = PortableErrorMessage.fromMessage(ErrorMessage.of(id, text));
        assertThat(portableErrorMessage.getId()).isEqualTo(id);
        assertThat(portableErrorMessage.getText()).isEqualTo(text);
    }

    @Test
    void equals_hashCode_toString() {
        String id = "1";
        String text = "error message";
        ErrorMessage message = ErrorMessage.of(id, text);
        PortableErrorMessage portableErrorMessage = PortableErrorMessage.fromMessage(message);

        assertThat(portableErrorMessage).isNotEqualTo(null)
                // equals()
                .isNotEqualTo(new PortableErrorMessage("", text))
                .isNotEqualTo(new PortableErrorMessage(id, ""))
                .isNotEqualTo(message)
                .isEqualTo(portableErrorMessage)
                .isEqualTo(new PortableErrorMessage(id, text))
                // hasCode()
                .hasSameHashCodeAs(new PortableErrorMessage(id, text))
                // toString()
                .asString().contains(id, text);
    }
}
