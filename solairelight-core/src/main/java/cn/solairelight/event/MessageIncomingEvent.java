package cn.solairelight.event;

import org.springframework.web.reactive.socket.WebSocketMessage;

/**
 * @author Joel Ou
 */
@EventType(EventContext.EventType.INCOMING_MESSAGE)
public interface MessageIncomingEvent extends Event<WebSocketMessage> {
}
