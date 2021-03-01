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

package org.optaweb.vehiclerouting.service.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import javax.enterprise.event.Event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErrorListenerTest {

    @Captor
    private ArgumentCaptor<ErrorMessage> argumentCaptor;

    @Test
    void should_pass_error_message_to_consumer(@Mock Event<ErrorMessage> errorMessageEvent) {
        // arrange
        String text = "error";
        ErrorListener errorListener = new ErrorListener(errorMessageEvent);
        // act
        errorListener.onErrorEvent(new ErrorEvent(this, text));
        // assert
        verify(errorMessageEvent).fire(argumentCaptor.capture());
        ErrorMessage capturedMessage = argumentCaptor.getValue();
        assertThat(capturedMessage.text).isEqualTo(text);
        assertThat(capturedMessage.id).isNotNull();
    }
}
