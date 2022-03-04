/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
