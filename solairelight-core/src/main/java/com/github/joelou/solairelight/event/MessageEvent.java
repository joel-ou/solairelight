package com.github.joelou.solairelight.event;

import com.github.joelou.solairelight.MessageWrapper;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.MESSAGE)
public interface MessageEvent extends SolairelightEvent<MessageWrapper> {
}
