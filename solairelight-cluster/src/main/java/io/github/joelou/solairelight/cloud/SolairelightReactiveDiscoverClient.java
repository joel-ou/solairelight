package io.github.joelou.solairelight.cloud;

import io.github.joelou.solairelight.cluster.ClusterTools;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Ou
 */
public class SolairelightReactiveDiscoverClient implements ReactiveDiscoveryClient {

    @Override
    public String description() {
        return "solairelight reactive discover client";
    }

    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        if(!serviceId.equals(ClusterTools.SOLAIRELIGHTS_SERVICE_ID)) {
            return Flux.empty();
        }
        return SolairelightRedisClient.getInstance().getNodeCacheFlux().filter(node->{
            //filter unhealthy node.
            return System.currentTimeMillis() - node.getVersion() < Duration.ofSeconds(2).toMillis();
        }).map(node->{
            NodeData.BasicInfo basicInfo = node.getBasicInfo();
            URI uri = URI.create(basicInfo.getUrl());
            Map<String, String> metadata = new HashMap<>();
            metadata.put(ClusterTools.SOLAIRELIGHTS_SESSION_QUOTA_KEY, String.valueOf(node.getSessionQuota().intValue()));
            return new DefaultServiceInstance(basicInfo.getNodeId(),
                    ClusterTools.SOLAIRELIGHTS_SERVICE_ID, uri.getHost(), uri.getPort(), uri.getScheme().equals("https"),
                    metadata);
        });
    }

    @Override
    public Flux<String> getServices() {
        return Flux.just(ClusterTools.SOLAIRELIGHTS_SERVICE_ID);
    }
}
