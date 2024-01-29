package io.github.joelou.solairelight.config;

import io.github.joelou.solairelight.cloud.SolairelightReactiveDiscoverClient;
import io.github.joelou.solairelight.cluster.ClusterTools;
import io.github.joelou.solairelight.gateway.BroadcastGlobalFilter;
import io.github.joelou.solairelight.gateway.SoalireSocketRedirectGlobalFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * @author Joel Ou
 */
@Configuration
@Import(SolairelightClusterConfiguration.class)
@LoadBalancerClients({
        @LoadBalancerClient(
                value = ClusterTools.SOLAIRELIGHTS_SERVICE_ID,
                configuration = SolairelightLoadBalancerConfiguration.class)
})
public class SolairelightGatewayConfiguration {

    //gateway config
    @Bean
    public SolairelightReactiveDiscoverClient solairelightReactiveDiscoverClient(ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate){
        return new SolairelightReactiveDiscoverClient();
    }

    @Bean
    public BroadcastGlobalFilter broadcastGlobalFilter(){
        return new BroadcastGlobalFilter();
    }

    @Bean
    @ConditionalOnProperty(value = "solairelight.websocket.routing.mode", havingValue = SoalireSocketRedirectGlobalFilter.ROUTING_MODEL_REDIRECT)
    public SoalireSocketRedirectGlobalFilter soalireSocketRedirectGlobalFilter(LoadBalancerClientFactory loadBalancerClientFactory){
        return new SoalireSocketRedirectGlobalFilter(loadBalancerClientFactory);
    }
}
