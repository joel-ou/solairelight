package cn.muskmelon.event;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.INCOMING_MESSAGE)
public interface MessageIncomingEvent<T> extends Event<T> {
}
