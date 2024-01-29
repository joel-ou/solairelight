package io.github.joelou.solairelight.gateway;

import io.github.joelou.solairelight.cluster.BroadcastDistributor;
import io.github.joelou.solairelight.cluster.ClusterTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author Joel Ou
 */
@Slf4j
public class BroadcastGlobalFilter implements GlobalFilter {
    @Resource
    private BroadcastDistributor broadcastDistributor;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || (!ClusterTools.SOLAIRELIGHTS_SERVICE_ID.equals(url.getScheme()) && !ClusterTools.SOLAIRELIGHTS_SERVICE_ID.equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        BroadcastHttpResponse response = new BroadcastHttpResponse();
        if(!url.getHost().equals("broadcast")){
            throw new RuntimeException("unsupported solaire host.");
        }
        setAlreadyRouted(exchange);
        return exchange.getRequest().getBody()
                //do broadcast.
                .flatMap(message-> broadcastDistributor.distributeAllNode(message))
                .doOnNext(response::add)
                .last()
                .map(x->response)
                .doOnError(e-> log.error("unexpected broadcast failed.", e))
                .onErrorResume(e-> Mono.just(BroadcastHttpResponse.failure("unexpected failed")))
                .flatMap(res->{
                    return BodyInserters
                            .fromValue(res)
                            .insert(exchange.getResponse(), new BodyInserterContext());
                })
                .flatMap(m->chain.filter(exchange));
    }
}
