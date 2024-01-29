package io.github.joelou.solairelight.cloud;

import io.github.joelou.solairelight.cluster.ClusterTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

/**
 * @author Joel Ou
 */
@Slf4j
public class SolairelightReactiveLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private final String serviceId;
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public SolairelightReactiveLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        if(!serviceId.equals(ClusterTools.SOLAIRELIGHTS_SERVICE_ID)){
            log.error("SolairelightReactiveLoadBalancer only use for solairelight.");
            return Mono.just(new EmptyResponse());
        }
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(this::getInstanceResponse);
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        ServiceInstance instance = instances.stream()
                .max(Comparator.comparingInt(in->Integer.parseInt(in.getMetadata()
                        .get(ClusterTools.SOLAIRELIGHTS_SESSION_QUOTA_KEY))))
                .get();
        return new DefaultResponse(instance);
    }
}
