package cn.solairelight.event;

import org.springframework.web.reactive.socket.WebSocketMessage;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.OUTGOING_MESSAGE)
public interface MessageOutgoingEvent extends Event<WebSocketMessage>{
}
