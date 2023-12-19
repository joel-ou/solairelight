package cn.solairelight.event;

import cn.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.SESSION_CONNECTED)
public interface SessionConnectedEvent extends Event<BasicSession> {
}
