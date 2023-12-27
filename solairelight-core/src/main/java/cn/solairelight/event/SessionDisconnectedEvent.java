package cn.solairelight.event;

import cn.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.SESSION_DISCONNECTED)
public interface SessionDisconnectedEvent extends Event<BasicSession> {
}
