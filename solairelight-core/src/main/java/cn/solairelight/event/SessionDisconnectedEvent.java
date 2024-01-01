package cn.solairelight.event;

import cn.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.SESSION_DISCONNECTED)
public interface SessionDisconnectedEvent extends SolairelightEvent<BasicSession> {
}
