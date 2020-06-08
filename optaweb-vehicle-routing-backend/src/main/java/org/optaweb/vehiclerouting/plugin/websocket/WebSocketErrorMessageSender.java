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

package org.optaweb.vehiclerouting.plugin.websocket;

import org.optaweb.vehiclerouting.service.error.ErrorMessage;
import org.optaweb.vehiclerouting.service.error.ErrorMessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Broadcasts application errors to interested clients over WebSocket.
 */
@Component
class WebSocketErrorMessageSender implements ErrorMessageConsumer {

    static final String TOPIC_ERROR = "/topic/error";

    private final SimpMessagingTemplate webSocket;

    @Autowired
    WebSocketErrorMessageSender(SimpMessagingTemplate webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void consumeMessage(ErrorMessage message) {
        webSocket.convertAndSend(TOPIC_ERROR, PortableErrorMessage.fromMessage(message));
    }
}
