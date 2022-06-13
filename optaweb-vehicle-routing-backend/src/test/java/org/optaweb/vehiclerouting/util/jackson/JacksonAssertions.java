package org.optaweb.vehiclerouting.util.jackson;

public class JacksonAssertions {

    public static <T> ObjectAssert<T> assertThat(T actual) {
        return new ObjectAssert<>(actual);
    }

    public static JsonAssert assertThat(String actual) {
        return new JsonAssert(actual);
    }
}
