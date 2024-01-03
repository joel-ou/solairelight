package io.github.joelou.solairelight.event;

import io.github.joelou.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.SESSION_DISCONNECTED)
public interface SessionDisconnectedEvent extends SolairelightEvent<BasicSession> {
}
