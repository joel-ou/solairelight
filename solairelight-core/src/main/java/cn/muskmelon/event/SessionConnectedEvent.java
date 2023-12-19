package cn.muskmelon.event;

import cn.muskmelon.session.BasicSession;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.SESSION_CONNECTED)
public interface SessionConnectedEvent extends Event<BasicSession> {
}
