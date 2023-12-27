package cn.solairelight.event;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Joel Ou
 */
@Builder
@Getter
@ToString
public class EventContext<T> {

    public enum EventType {
        SESSION_CONNECTED,
        SESSION_DISCONNECTED,
        INCOMING_MESSAGE,
        OUTGOING_MESSAGE,
        GLOBAL,
    }

    private EventType eventType;

    private String trigger;

    private T argument;
}
