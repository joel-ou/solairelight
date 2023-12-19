package cn.muskmelon.event;

import cn.muskmelon.session.BasicSession;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.muskmelon.session.SessionBroker;
import cn.muskmelon.session.index.IndexService;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class SessionCleanup implements SessionDisconnectedEvent<BasicSession> {

    @Resource
    private IndexService indexService;

    @Override
    public void apply(EventContext<BasicSession> context) {
        String sessionId = context.getArgument().getSessionId();
        SessionBroker.getStorage().invalidate(sessionId);
        indexService.deleteBySessionId(sessionId);
    }
}
