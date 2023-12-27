package cn.solairelight.runner.demo.event;

import cn.solairelight.event.EventContext;
import cn.solairelight.event.SessionConnectedEvent;
import cn.solairelight.event.SessionDisconnectedEvent;
import cn.solairelight.session.BasicSession;
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
        log.info("disconnected event triggered. {}", context);
    }
}
