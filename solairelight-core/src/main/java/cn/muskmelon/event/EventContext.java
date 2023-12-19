package cn.muskmelon.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author Joel Ou
 */
@Builder
@Data
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
