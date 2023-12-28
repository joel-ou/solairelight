package cn.solairelight.runner.demo.event;

import cn.solairelight.event.EventContext;
import cn.solairelight.event.SessionConnectedEvent;
import cn.solairelight.session.BasicSession;
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
        System.out.println(context.getArgument().getSessionId());
        log.info("connected event triggered. {}", context);
    }
}
