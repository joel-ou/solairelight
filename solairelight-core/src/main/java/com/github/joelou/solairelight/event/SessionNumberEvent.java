package com.github.joelou.solairelight.event;

import com.github.joelou.solairelight.cluster.NodeData;
import com.github.joelou.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
public class SessionNumberEvent implements SessionConnectedEvent, SessionDisconnectedEvent {

    @Override
    public void execute(EventContext<BasicSession> context) {
        switch (context.getTrigger()) {
            case SESSION_CONNECTED:
                NodeData.instance.getSessionNumber().decrementAndGet();
                break;
            case SESSION_DISCONNECTED:
                NodeData.instance.getSessionNumber().incrementAndGet();
                break;
        }
    }
}
