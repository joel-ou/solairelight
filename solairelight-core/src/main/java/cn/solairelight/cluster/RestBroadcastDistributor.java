package cn.solairelight.cluster;

import cn.solairelight.brodcast.BroadcastParam;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

/**
 * @author Joel Ou
 */
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
                .filter(basicInfo -> !basicInfo.getNodeId().equals(ClusterTools.getNodeId()))
                .flatMap(basicInfo -> post(broadcastParam, basicInfo))
                .publishOn(scheduler);
    }

    private Mono<DistributeResult> post(BroadcastParam broadcastParam, NodeData.BasicInfo basicInfo){
        String uri = basicInfo.getIpAddress();
        return DistributeWebClient
                .post(URI.create(uri), broadcastParam)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .map(o->new DistributeResult(basicInfo.getNodeId(), o.getStatusCode()));
    }
}
