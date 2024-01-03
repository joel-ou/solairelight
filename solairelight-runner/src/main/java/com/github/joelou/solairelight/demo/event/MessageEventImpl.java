package com.github.joelou.solairelight.demo.event;

import com.github.joelou.solairelight.MessageWrapper;
import com.github.joelou.solairelight.event.EventContext;
import com.github.joelou.solairelight.event.MessageEvent;
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
        System.out.println(context.getArgument().isForwardable());
        log.info("message event. {}", context);
    }
}
