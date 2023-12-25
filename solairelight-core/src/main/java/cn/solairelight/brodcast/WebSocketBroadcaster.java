package cn.solairelight.brodcast;

import cn.solairelight.cluster.BroadcastDistributor;
import cn.solairelight.cluster.ClusterTools;
import cn.solairelight.cluster.NodeData;
import cn.solairelight.cluster.SolairelightRedisClient;
import cn.solairelight.properties.SolairelightProperties;
import cn.solairelight.session.BasicSession;
import cn.solairelight.session.SessionFinder;
import cn.solairelight.session.WebSocketSessionExpand;
import cn.solairelight.exception.NoSessionFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

/**
 * @author Joel Ou
 */
@Service
@Slf4j
public class WebSocketBroadcaster implements Broadcaster {

    @Resource
    private SessionFinder sessionFinder;

    @Resource
    private BroadcastDistributor broadcastDistributor;

    @Resource
    private SolairelightProperties solairelightProperties;

    @Override
    public boolean broadcast(BroadcastParam broadcastParam) {
        boolean standalone = solairelightProperties.isStandalone();
        try {
            if(broadcastParam.getRange().equals("*")){
                localBroadcast(null, broadcastParam);
            } else {
                LinkedList<String[]> exprList = parseRange(broadcastParam.getRange());
                localBroadcast(exprList, broadcastParam);
            }
            if(!standalone)
                broadcastDistributor.distributeAllNode(broadcastParam).subscribe();
        } catch (NoSessionFoundException e) {
            if(standalone){
                throw e;
            }
        }
        return true;
    }

    @Override
    public void localBroadcast(BroadcastParam broadcastParam){
        LinkedList<String[]> exprList = parseRange(broadcastParam.getRange());
        localBroadcast(exprList, broadcastParam);
    }

    @Override
    public void localBroadcast(LinkedList<String[]> exprList, BroadcastParam broadcastParam){
        Collection<BasicSession> sessions = sessionFinder.finding(exprList,
                broadcastParam.getPredicate());
        if(CollectionUtils.isEmpty(sessions)) {
            log.warn("node {} no session found.", ClusterTools.getNodeId());
            throw new NoSessionFoundException();
        }
        for (BasicSession session : sessions) {
            WebSocketSessionExpand webSocketSession = ((WebSocketSessionExpand) session);
            //send message for client
            webSocketSession.getSink().next(webSocketSession.getOriginalSession().textMessage(broadcastParam.getMessage()));
        }
        log.info("broadcast success {} matched", sessions.size());
    }

    private LinkedList<String[]> parseRange(String range){
        LinkedList<String[]> exprList = new LinkedList<>();
        String[] expr = range.split(",");
        for (String e : expr) {
            String[] kv = e.split("=");
            if(kv[0].equals("id")) {
                exprList.addFirst(kv);
            } else {
                exprList.add(kv);
            }
        }
        return exprList;
    }
}
