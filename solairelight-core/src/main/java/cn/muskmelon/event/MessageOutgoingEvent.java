package cn.muskmelon.event;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.OUTGOING_MESSAGE)
public interface MessageOutgoingEvent<T> extends Event<T>{
}
