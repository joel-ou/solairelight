package io.github.joelou.solairelight.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author Joel Ou
 */
public class SoalireSocketRedirectGlobalFilter implements GlobalFilter, Ordered {
    public final static String ROUTING_MODEL_REDIRECT = "redirect";

    private final LoadBalancerClientFactory clientFactory;

    public SoalireSocketRedirectGlobalFilter(LoadBalancerClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        if(url == null) {
            return chain.filter(exchange);
        }
        String scheme = url.getScheme();
        if ((!"ws".equals(scheme) && !"wss".equals(scheme))) {
            return chain.filter(exchange);
        }
        if (!exchange.getResponse().isCommitted()) {
            setResponseStatus(exchange, HttpStatus.TEMPORARY_REDIRECT);
            final ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().set(HttpHeaders.LOCATION, url.toString());
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER+1;
    }
}
