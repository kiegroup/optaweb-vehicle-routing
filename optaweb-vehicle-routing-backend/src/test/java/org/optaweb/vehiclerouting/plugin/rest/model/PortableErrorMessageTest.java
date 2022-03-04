/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
