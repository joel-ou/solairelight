package com.github.joelou.solairelight.brodcast;

import com.github.joelou.solairelight.cluster.BroadcastDistributor;
import com.github.joelou.solairelight.cluster.ClusterTools;
import com.github.joelou.solairelight.exception.DuplicatedBroadcastException;
import com.github.joelou.solairelight.exception.NoSessionFoundException;
import com.github.joelou.solairelight.properties.SolairelightProperties;
import com.github.joelou.solairelight.session.BasicSession;
import com.github.joelou.solairelight.session.SessionFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Joel Ou
 */
@Service
@Slf4j
public class WebSocketBroadcaster extends AbstractBroadcaster {
    @Resource
    private SessionFinder sessionFinder;

    @Resource
    private BroadcastDistributor broadcastDistributor;

    @Resource
    private SolairelightProperties solairelightProperties;

    @Resource
    private BroadcastSender broadcastSender;

    @Override
    public boolean broadcast(BroadcastParam broadcastParam) {
        boolean clusterEnable = solairelightProperties.getCluster().isEnable();
        if(clusterEnable)
            broadcastDistributor.distributeAllNode(broadcastParam).subscribe();
        try {
            if(broadcastParam.getRange().equals("*")){
                localBroadcast(null, broadcastParam);
            } else {
                LinkedList<String[]> exprList = parseRange(broadcastParam.getRange());
                localBroadcast(exprList, broadcastParam);
            }
        } catch (NoSessionFoundException e) {
            if(!clusterEnable){
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
        if(super.duplicated(broadcastParam.getId())){
            log.info("duplicated broadcast {}", broadcastParam);
            if(solairelightProperties.getCluster().isEnable()) {
                return;
            }
            throw new DuplicatedBroadcastException();
        }
        Collection<BasicSession> sessions = sessionFinder.finding(exprList,
                broadcastParam.getPredicate());
        if(CollectionUtils.isEmpty(sessions)) {
            log.warn("node {} no session found.", ClusterTools.getNodeId());
            throw new NoSessionFoundException();
        }
        for (BasicSession session : sessions) {
            //send message for client
            broadcastSender.send(session, broadcastParam.getMessage());
            //store the broadcast id.
            super.cache(broadcastParam.getId());
        }
        log.info("broadcast success {} matched", sessions.size());
    }

    private LinkedList<String[]> parseRange(String range){
        LinkedList<String[]> exprList = new LinkedList<>();
        String[] expr = range.split(",");
        for (String e : expr) {
            String[] kv = e.split("==");
            if(kv[0].equals("id")) {
                exprList.addFirst(kv);
            } else {
                exprList.add(kv);
            }
        }
        return exprList;
    }
}
