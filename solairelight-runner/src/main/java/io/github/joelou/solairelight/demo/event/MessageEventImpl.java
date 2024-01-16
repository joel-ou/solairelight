package io.github.joelou.solairelight.demo.event;

import io.github.joelou.solairelight.MessageWrapper;
import io.github.joelou.solairelight.event.EventContext;
import io.github.joelou.solairelight.event.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class MessageEventImpl implements MessageEvent {

    @Override
    public void execute(EventContext<MessageWrapper> context) {
        log.info("message event. {}", context);
    }
}
