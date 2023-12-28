package cn.solairelight.runner.demo.event;

import cn.solairelight.MessageWrapper;
import cn.solairelight.event.EventContext;
import cn.solairelight.event.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class MessageEventImpl implements MessageEvent {

    @Override
    public void execute(EventContext<MessageWrapper> context) {
        System.out.println(context.getArgument().isForwardable());
        log.info("message event. {}", context);
    }
}
