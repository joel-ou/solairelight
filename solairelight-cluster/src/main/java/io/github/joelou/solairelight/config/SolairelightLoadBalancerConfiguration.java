package io.github.joelou.solairelight.config;

import io.github.joelou.solairelight.cloud.SolairelightReactiveLoadBalancer;
import io.github.joelou.solairelight.cluster.ClusterTools;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Joel Ou
 */
public class SolairelightLoadBalancerConfiguration {

    @Bean
    ReactorLoadBalancer<ServiceInstance> solairelightReactiveLoadBalancer(Environment environment,
                                                            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = ClusterTools.SOLAIRELIGHTS_SERVICE_ID;
        return new SolairelightReactiveLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }
}
