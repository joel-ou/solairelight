package io.github.joelou.solairelight.config;

import io.github.joelou.solairelight.cloud.SolairelightReactiveDiscoverClient;
import io.github.joelou.solairelight.cluster.ClusterTools;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
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
public class SolairelightCloudConfiguration {

    @Bean
    public SolairelightReactiveDiscoverClient solairelightReactiveDiscoverClient(ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate){
        return new SolairelightReactiveDiscoverClient();
    }
}
