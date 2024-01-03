package com.github.joelou.solairelight.event;

import com.github.joelou.solairelight.cluster.NodeData;
import com.github.joelou.solairelight.session.BasicSession;
import lombok.extern.slf4j.Slf4j;
import com.github.joelou.solairelight.session.SessionBroker;
import com.github.joelou.solairelight.session.index.IndexService;
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
