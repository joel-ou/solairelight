package cn.solairelight.event;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.OUTGOING_MESSAGE)
public interface MessageOutgoingEvent<T> extends Event<T>{
}
