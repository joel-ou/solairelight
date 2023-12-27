package cn.solairelight.event;

import cn.solairelight.session.BasicSession;
import lombok.extern.slf4j.Slf4j;
import cn.solairelight.session.SessionBroker;
import cn.solairelight.session.index.IndexService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class SessionCleanup implements SessionDisconnectedEvent {

    @Resource
    private IndexService indexService;

    @Override
    public void execute(EventContext<BasicSession> context) {
        String sessionId = context.getArgument().getSessionId();
        SessionBroker.getStorage().invalidate(sessionId);
        indexService.deleteBySessionId(sessionId);
    }
}
