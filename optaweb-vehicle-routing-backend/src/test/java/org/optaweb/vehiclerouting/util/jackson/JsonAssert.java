package org.optaweb.vehiclerouting.util.jackson;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.StringAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonAssert extends StringAssert {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected JsonAssert(String actual) {
        super(actual);
    }

    public void deserializedIsEqualTo(Object expected) {
        isNotNull();
        try {
            Object value = objectMapper.readValue(actual, expected.getClass());
            Assertions.assertThat(value).isEqualTo(expected);
        } catch (JsonProcessingException e) {
            throw new AssertionError("ObjectMapper.readValue(actual, expected.getClass()) called with actual: <" + actual
                    + "> and expected: <" + e
                    + "> threw an exception.", e);
        }
    }
}
