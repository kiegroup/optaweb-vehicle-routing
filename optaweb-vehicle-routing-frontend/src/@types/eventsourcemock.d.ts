// See https://github.com/gcedo/eventsourcemock

declare module 'eventsourcemock' {
  interface EventSource {
    onopen: () => {};
    onerror: () => {};

    emit(eventName: string, messageEvent?: MessageEvent);

    emitOpen();
  }

  let sources: { [key: string]: EventSource };
}
