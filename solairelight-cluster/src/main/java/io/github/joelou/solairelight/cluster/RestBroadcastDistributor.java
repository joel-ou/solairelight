package io.github.joelou.solairelight.cluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import javax.annotation.Resource;
import java.net.ConnectException;
import java.time.Duration;

/**
 * @author Joel Ou
 */
@Slf4j
@Service
public class RestBroadcastDistributor implements BroadcastDistributor {
    private final Scheduler scheduler = Schedulers.boundedElastic();

    @Resource
    private ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate;

    @Override
    public Flux<DistributeResult> distributeSpecified(Object broadcastParam,
                                                      NodeData.BasicInfo basicInfo) {
        return post(broadcastParam, basicInfo).flux();
    }

    @Override
    public Flux<DistributeResult> distributeAllNode(Object broadcastParam) {
        return SolairelightRedisClient
                .getInstance()
                .getNodes()
                .filter(nodeData -> !nodeData.getBasicInfo().getNodeId().equals(ClusterTools.getNodeId()))
                .flatMap(nodeData -> post(broadcastParam, nodeData.getBasicInfo()));
    }

    private Mono<DistributeResult> post(Object broadcastParam, NodeData.BasicInfo basicInfo){
        log.info("distribute to node {}.", basicInfo);
        String uri = basicInfo.getUrl();
        uri = String.format("%s/solairelight/distributor/entrance", uri);
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
                        return DistributeResult.failure(basicInfo, ClusterExceptionEnum.DISTRIBUTE_FAILED_HTTP);
                    }
                })
                .onErrorResume((e)-> {
                    NodeDataCacheStorage.failed(basicInfo);
                    log.error("error occurred when distribute to node {}.", basicInfo);
                    return Mono.just(DistributeResult.failure(basicInfo, "distribute failed."));
                });
    }
}
