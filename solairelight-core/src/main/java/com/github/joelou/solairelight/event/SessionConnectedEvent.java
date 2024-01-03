package com.github.joelou.solairelight.event;

import com.github.joelou.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.SESSION_CONNECTED)
public interface SessionConnectedEvent extends SolairelightEvent<BasicSession> {
}
