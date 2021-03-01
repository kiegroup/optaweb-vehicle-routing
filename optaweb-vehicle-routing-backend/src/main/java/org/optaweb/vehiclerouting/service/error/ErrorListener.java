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

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Creates messages from error events and passes them to consumers.
 */
@ApplicationScoped
public class ErrorListener {

    private final Event<ErrorMessage> errorMessageEvent;

    @Inject
    public ErrorListener(Event<ErrorMessage> errorMessageEvent) {
        this.errorMessageEvent = errorMessageEvent;
    }

    public void onErrorEvent(@Observes ErrorEvent event) {
        errorMessageEvent.fire(ErrorMessage.of(UUID.randomUUID().toString(), event.message));
    }
}
