package cn.solairelight.event;

import cn.solairelight.MessageWrapper;

/**
 * @author Joel Ou
 */
@SolairelightEventType(EventContext.EventType.MESSAGE)
public interface MessageEvent extends SolairelightEvent<MessageWrapper> {
}
