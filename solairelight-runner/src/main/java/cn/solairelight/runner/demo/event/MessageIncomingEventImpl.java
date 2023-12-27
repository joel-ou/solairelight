package cn.solairelight.runner.demo.event;

import cn.solairelight.event.EventContext;
import cn.solairelight.event.MessageIncomingEvent;
import cn.solairelight.event.SessionConnectedEvent;
import cn.solairelight.session.BasicSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class MessageIncomingEventImpl implements MessageIncomingEvent {
    @Override
    public void execute(EventContext<WebSocketMessage> context) {
        log.info("message incoming event triggered. {}", context.getArgument().getPayloadAsText());
    }
}
