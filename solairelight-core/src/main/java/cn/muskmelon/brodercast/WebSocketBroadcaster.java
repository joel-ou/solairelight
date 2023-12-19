package cn.muskmelon.brodercast;

import cn.muskmelon.session.BasicSession;
import cn.muskmelon.session.SessionFinder;
import cn.muskmelon.session.WebSocketSessionExpand;
import jakarta.annotation.Resource;
import cn.muskmelon.exception.NoSessionFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author Joel Ou
 */
@Service
@Slf4j
public class WebSocketBroadcaster implements Broadcaster {

    @Resource
    private SessionFinder sessionFinder;

    @Override
    public boolean broadcast(BroadcastParam broadcastParam) {
        Collection<BasicSession> sessions = sessionFinder.finding(broadcastParam.getRange(),
                broadcastParam.getPredicate());
        if(CollectionUtils.isEmpty(sessions)) {
            throw new NoSessionFoundException();
        }
        for (BasicSession session : sessions) {
            WebSocketSessionExpand webSocketSession = ((WebSocketSessionExpand) session);
            //send message for client
            webSocketSession.getSink().next(webSocketSession.getOriginalSession().textMessage(broadcastParam.getMessage()));
        }
        log.info("broadcast success {} matched", sessions.size());
        return true;
    }
}
