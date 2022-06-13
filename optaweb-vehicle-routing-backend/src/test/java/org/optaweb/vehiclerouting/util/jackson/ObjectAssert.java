package org.optaweb.vehiclerouting.util.jackson;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectAssert<T> extends AbstractAssert<ObjectAssert<T>, T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected ObjectAssert(T actual) {
        super(actual, ObjectAssert.class);
    }

    public void serializedIsEqualToJson(String expected) {
        isNotNull();
        try {
            String actualSerialized = objectMapper.writeValueAsString(actual);
            Assertions.assertThat(actualSerialized).isEqualToIgnoringWhitespace(expected);
        } catch (JsonProcessingException e) {
            throw new AssertionError("ObjectMapper.writeValueAsString(actual) called with actual: <" + actual
                    + "> threw an exception.", e);
        }
    }
}
