// See https://github.com/gcedo/eventsourcemock

declare module 'eventsourcemock' {
  interface EventSource {
    onopen: () => unknown;
    onerror: () => unknown;

    emit(eventName: string, messageEvent?: MessageEvent);

    emitOpen();
  }

  let sources: { [key: string]: EventSource };
}
