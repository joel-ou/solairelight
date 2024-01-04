package io.github.joelou.solairelight.demo.event;

import io.github.joelou.solairelight.event.EventContext;
import io.github.joelou.solairelight.event.SessionDisconnectedEvent;
import io.github.joelou.solairelight.session.BasicSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class SessionDisconnectedEventImpl implements SessionDisconnectedEvent {

    @Override
    public void execute(EventContext<BasicSession> context) {
        System.out.println(context.getArgument().getSessionId());
        log.info("disconnected event triggered. {}", context);
    }
}
