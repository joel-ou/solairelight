package io.github.joelou.solairelight.event;

import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.session.BasicSession;
import lombok.extern.slf4j.Slf4j;
import io.github.joelou.solairelight.session.SessionBroker;
import io.github.joelou.solairelight.session.index.IndexService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class SessionCleanupEvent implements SessionDisconnectedEvent {

    @Resource
    private IndexService indexService;

    @Override
    public void execute(EventContext<BasicSession> context) {
        String sessionId = context.getArgument().getSessionId();
        SessionBroker.getStorage().invalidate(sessionId);
        indexService.deleteBySessionId(sessionId);
        //recover session number.
        NodeData.instance.getSessionNumber().incrementAndGet();
    }
}
