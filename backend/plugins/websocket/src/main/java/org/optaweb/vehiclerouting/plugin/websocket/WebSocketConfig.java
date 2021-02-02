/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Configures STOMP over WebSocket.
 * <p>
 * See <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-enable">
 * WebSockets/Enable STOMP</a>.
 */
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /vrp-websocket is the HTTP URL for the endpoint to which a WebSocket client needs to connect
        // for the WebSocket handshake.
        registry
                .addEndpoint("/vrp-websocket")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // STOMP messages whose destination header begins with /app are routed to @MessageMapping methods
        // in @Controller classes.
        // Not sure if "/topic" should be an application prefix but I couldn't get @SubscribeMapping("/route") working
        // without this.
        registry.setApplicationDestinationPrefixes("/app", "/topic");
        // Use the built-in message broker for subscriptions and broadcasting,
        // and route messages whose destination header begins with /topic to the broker.
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        // TODO Reduce WebSocket message sizes to minimum to avoid having to increase the session buffer size.
        // Read setSendBufferSizeLimit() Javadoc to get some insight.
        // For example: "In general WebSocket servers expect that messages to a single WebSocket session are sent
        // from a single thread at a time."
        // FIXME Remove this temporary workaround.
        registry.setSendBufferSizeLimit(2 * 1024 * 1024);
    }
}
