package cn.solairelight.runner.demo.event;

import cn.solairelight.event.EventContext;
import cn.solairelight.event.MessageIncomingEvent;
import cn.solairelight.event.MessageOutgoingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class MessageOutgoingEventImpl implements MessageOutgoingEvent {

    @Override
    public void execute(EventContext<WebSocketMessage> context) {
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("message outgoing event triggered. {}", context.getArgument().getPayloadAsText());
    }
}
