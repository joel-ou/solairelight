package io.github.joelou.solairelight.event;

import io.github.joelou.solairelight.MessageWrapper;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.MESSAGE)
public interface MessageEvent extends SolairelightEvent<MessageWrapper> {
}
