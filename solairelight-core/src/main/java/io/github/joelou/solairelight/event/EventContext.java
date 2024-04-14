package io.github.joelou.solairelight.event;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Joel Ou
 */
@Getter
@ToString
public class EventContext<T> {

    public enum EventType {
        SESSION_CONNECTED,
        SESSION_DISCONNECTED,
        MESSAGE,
        GLOBAL;
    }

    private EventContext() {
    }

    public static <T> EventContext<T> create() {
        return new EventContext<>();
    }

    private EventType eventType;

    private EventTrigger.TriggerAction trigger;

    private T argument;

    public EventContext<T> setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventContext<T> setTrigger(EventTrigger.TriggerAction trigger) {
        this.trigger = trigger;
        return this;
    }

    EventContext<T> setArgument(T argument) {
        this.argument = argument;
        return this;
    }
}
