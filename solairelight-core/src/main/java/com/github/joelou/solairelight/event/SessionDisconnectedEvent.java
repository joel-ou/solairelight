package com.github.joelou.solairelight.event;

import com.github.joelou.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.SESSION_DISCONNECTED)
public interface SessionDisconnectedEvent extends SolairelightEvent<BasicSession> {
}
