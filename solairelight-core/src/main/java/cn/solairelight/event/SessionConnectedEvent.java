package cn.solairelight.event;

import cn.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.SESSION_CONNECTED)
public interface SessionConnectedEvent extends SolairelightEvent<BasicSession> {
}
