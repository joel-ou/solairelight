package io.github.joelou.solairelight.cluster;

import io.github.joelou.solairelight.brodcast.BroadcastParam;
import io.github.joelou.solairelight.exception.ExceptionEnum;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;

/**
 * @author Joel Ou
 */
@Slf4j
@Service
public class RestBroadcastDistributor implements BroadcastDistributor {
    private final Scheduler scheduler = Schedulers.boundedElastic();

    @Override
    public Flux<DistributeResult> distributeSpecified(BroadcastParam broadcastParam,
                                                      NodeData.BasicInfo basicInfo) {
        return post(broadcastParam, basicInfo).flux();
    }

    @Override
    public Flux<DistributeResult> distributeAllNode(BroadcastParam broadcastParam) {
        return SolairelightRedisClient
                .getInstance()
                .getNodes()
                .filter(nodeData -> !nodeData.getBasicInfo().getNodeId().equals(ClusterTools.getNodeId()))
                .flatMap(nodeData -> post(broadcastParam, nodeData.getBasicInfo()));
    }

    private Mono<DistributeResult> post(BroadcastParam broadcastParam, NodeData.BasicInfo basicInfo){
        log.info("distribute to node {}.", basicInfo);
        String uri = basicInfo.getUrl();
        uri = String.format("%s/solairelight/distributor/entrance", uri);
        return DistributeWebClient
                .post(uri, broadcastParam)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).filter(e->e instanceof ConnectException))
                .map(response->{
                    if(response.getStatusCode().is2xxSuccessful()) {
                        log.info("distribute done node {} and result: {}", basicInfo, response);
                        return response.getBody();
                    } else {
                        log.error("distribute done node {} and result: {}", basicInfo, "http request failed");
                        return DistributeResult.failure(basicInfo, ExceptionEnum.DISTRIBUTE_FAILED_HTTP);
                    }
                })
                .onErrorResume((e)-> {
                    log.error("error occurred when distribute to node {}.", basicInfo);
                    return Mono.just(DistributeResult.failure(basicInfo, "distribute failed."));
                });
    }
}
