package cn.muskmelon.event;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.SESSION_DISCONNECTED)
public interface SessionDisconnectedEvent<T> extends Event<T> {
}
