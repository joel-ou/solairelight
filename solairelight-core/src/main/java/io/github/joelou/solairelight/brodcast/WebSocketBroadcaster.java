package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.cluster.BroadcastDistributor;
import io.github.joelou.solairelight.cluster.ClusterTools;
import io.github.joelou.solairelight.cluster.DistributeResult;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.exception.DuplicatedBroadcastException;
import io.github.joelou.solairelight.exception.NoSessionFoundException;
import io.github.joelou.solairelight.exception.ResponseMessageException;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import io.github.joelou.solairelight.session.BasicSession;
import io.github.joelou.solairelight.session.SessionFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
    public Flux<DistributeResult> broadcast(BroadcastParam broadcastParam) {
        Flux<DistributeResult> clusterResult = broadcastDistributor.distributeAllNode(broadcastParam);
        Mono<DistributeResult> localResult;
        try {
            if(broadcastParam.getRange().equals("*")){
                localResult = localBroadcast(null, broadcastParam);
            } else {
                LinkedList<String[]> exprList = parseRange(broadcastParam.getRange());
                localResult = localBroadcast(exprList, broadcastParam);
            }
        } catch (ResponseMessageException e) {
            localResult = Mono.just(DistributeResult.failure(NodeData.instance.getBasicInfo(), e));
        }
        return clusterResult.concatWith(localResult);
    }

    @Override
    public Mono<DistributeResult> localBroadcast(BroadcastParam broadcastParam){
        LinkedList<String[]> exprList = parseRange(broadcastParam.getRange());
        return localBroadcast(exprList, broadcastParam);
    }

    @Override
    public Mono<DistributeResult> localBroadcast(LinkedList<String[]> exprList, BroadcastParam broadcastParam){
        if(super.duplicated(broadcastParam.getId())){
            log.info("duplicated broadcast {}", broadcastParam);
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
        return Mono.just(DistributeResult.success(NodeData.instance.getBasicInfo()));
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
