package io.github.joelou.solairelight.event;

import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
@Deprecated
public class SessionNumberEvent implements SessionConnectedEvent, SessionDisconnectedEvent {

    @Override
    public void execute(EventContext<BasicSession> context) {
        switch (context.getTrigger()) {
            case SESSION_CONNECTED:
                NodeData.instance.getSessionQuota().decrementAndGet();
                break;
            case SESSION_DISCONNECTED:
                NodeData.instance.getSessionQuota().incrementAndGet();
                break;
        }
    }
}
