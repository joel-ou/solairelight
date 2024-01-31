package io.github.joelou.solairelight.socket;

import io.github.joelou.solairelight.SolairelightSettings;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 */
@Component
@Slf4j
public class SessionNumberLimitWebFilter implements WebFilter {

    @Resource
    private SolairelightProperties solairelightProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //zero is no limit.
        if(solairelightProperties.getSession().getMaxNumber() == 0){
            return chain.filter(exchange);
        }
        String method = exchange.getRequest().getMethodValue();
        String header = exchange.getRequest().getHeaders().getUpgrade();
        if((method.equals("GET") && header != null && header.equalsIgnoreCase("websocket"))) {
            AtomicInteger sessionQuota = NodeData.instance.getSessionQuota();
            if(sessionQuota.get() <= 0){
                return limitExceeded(exchange);
            }
            //try to acquire a session number.
            if(sessionQuota.getAndAccumulate(0, (prev, x) -> prev > 0 ? prev - 1 : prev) > 0){
                return chain.filter(exchange);
            }
            return limitExceeded(exchange);
        }else {
            return chain.filter(exchange);
        }
    }

    @Deprecated
    private Mono<Void> redirect(ServerWebExchange serverWebExchange) {
        //check whether none-cluster.
        if (!SolairelightSettings.isCluster()) {
            log.error("the number of sessions exceeded. cur {} max {}",
                    NodeData.instance.getSessionQuota().get(),
                    solairelightProperties.getSession().getMaxNumber());
            return Mono.empty();
        }
        //get nodes of cluster to redirect.
        SolairelightRedisClient.getInstance().getNodeCache()
                .stream()
                .max(Comparator.comparingInt(obj -> obj.getSessionQuota().get()))
                .ifPresent(nodeData -> {
                    if (nodeData.getSessionQuota().get() <= 0) {
                        log.error("session rejected. no more session number on other nodes.");
                        return;
                    }
                    String uri = nodeData.getBasicInfo().getUrl();
                    uri += serverWebExchange.getRequest().getPath();
                    ServerHttpResponse response = serverWebExchange.getResponse();
                    response.setStatusCode(HttpStatus.FOUND);
                    response.getHeaders().add("Location", uri);
                    response.setComplete();
                    log.info("currently node session number exceeded, redirect to node {} uri {}",
                            nodeData.getBasicInfo().getNodeId(),
                            uri);
                });
        return Mono.empty();
    }

    private Mono<Void> limitExceeded(ServerWebExchange serverWebExchange){
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
        return response.setComplete();
    }
}
