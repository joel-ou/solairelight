package io.github.joelou.solairelight.runner.demo.event;

import io.github.joelou.solairelight.event.EventContext;
import io.github.joelou.solairelight.event.SessionConnectedEvent;
import io.github.joelou.solairelight.session.BasicSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class SessionConnectedEventImpl implements SessionConnectedEvent {

    @Override
    public void execute(EventContext<BasicSession> context) {
        log.info("connected event triggered. {}", context);
    }
}
