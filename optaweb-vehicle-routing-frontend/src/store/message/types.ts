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

import { Action } from 'redux';

export enum ActionType {
  RECEIVE_MESSAGE = 'RECEIVE_MESSAGE',
  READ_MESSAGE = 'READ_MESSAGE',
}

export interface ReceiveMessageAction extends Action<ActionType.RECEIVE_MESSAGE> {
  readonly payload: MessagePayload;
}

export interface ReadMessageAction extends Action<ActionType.READ_MESSAGE> {
  readonly id: string;
}

export type MessageAction = ReceiveMessageAction | ReadMessageAction;

export interface MessagePayload {
  readonly id: string;
  readonly text: string;
}

export interface Message extends MessagePayload {
  readonly status: 'new' | 'read';
}
