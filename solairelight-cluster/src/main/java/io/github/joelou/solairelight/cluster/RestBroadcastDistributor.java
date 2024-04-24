package io.github.joelou.solairelight.cluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Joel Ou
 */
@Slf4j
@Service
public class RestBroadcastDistributor implements BroadcastDistributor {

    @Override
    public Flux<NodeBroadcastingResponse> distributeSpecified(Object broadcastParam,
                                                              String id) {
        SolairelightRedisClient redisClient = SolairelightRedisClient.getInstance();
        AtomicReference<NodeData> jumping = new AtomicReference<>();
        return redisClient
                .getNodeById(id)
                .flatMap(nodeId-> redisClient.getNodes().filter(n->n.getBasicInfo().getNodeId().equals(nodeId)).last())
                .flatMap(node-> {
                    jumping.set(node);
                    return post(broadcastParam, node.getBasicInfo());
                })
                .flatMapMany(r->{
                    Flux<NodeBroadcastingResponse> responseFlux = Flux.just(r);
                    if(!r.isSuccess()){
                        return responseFlux.concatWith(distributeAllNode(broadcastParam, jumping.get()));
                    }
                    return responseFlux;
                });
    }

    @Override
    public Flux<NodeBroadcastingResponse> distributeAllNode(Object broadcastParam) {
        return distributeAllNode(broadcastParam, null);
    }

    private Flux<NodeBroadcastingResponse> distributeAllNode(Object broadcastParam, NodeData jumping) {
        return SolairelightRedisClient
                .getInstance()
                .getNodeCacheFlux()
                .filter(nodeData -> !nodeData.equals(jumping))
                .flatMap(nodeData -> post(broadcastParam, nodeData.getBasicInfo()));
    }

    private Mono<NodeBroadcastingResponse> post(Object broadcastParam, NodeData.BasicInfo basicInfo){
        log.info("distribute to node {}.", basicInfo);
        String uri = basicInfo.getUrl();
        uri = String.format("%s/solairelight/broadcast", uri);
        return DistributeWebClient
                .post(uri, broadcastParam)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).filter(e->e instanceof ConnectException))
                .map(response->{
                    if(response.getStatusCode().is2xxSuccessful()) {
                        NodeDataCacheStorage.recover(basicInfo);
                        log.info("distribute done node {} and result: {}", basicInfo, response);
                        return response.getBody();
                    } else {
                        log.error("distribute done node {} and result: {}", basicInfo, "http request failed");
                        return NodeBroadcastingResponse.failure(basicInfo, ClusterExceptionEnum.DISTRIBUTE_FAILED_HTTP);
                    }
                })
                .onErrorResume((e)-> {
                    NodeDataCacheStorage.failed(basicInfo);
                    log.error("error occurred when distribute to node {}.", basicInfo);
                    return Mono.just(NodeBroadcastingResponse.failure(basicInfo, "distribute failed."));
                });
    }
}
