package cn.solairelight.event;

import cn.solairelight.MessageWrapper;
import org.springframework.web.reactive.socket.WebSocketMessage;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.MESSAGE)
public interface MessageEvent extends Event<MessageWrapper<Object>> {
}
