package io.github.joelou.solairelight.gateway;

import io.github.joelou.solairelight.cluster.BroadcastDistributor;
import io.github.joelou.solairelight.cluster.NodeBroadcastingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author Joel Ou
 */
@Slf4j
public class BroadcastGlobalFilter implements GlobalFilter {
    @Resource
    private BroadcastDistributor broadcastDistributor;

    private final static Pattern pattern = Pattern.compile("id={2}((\\d+|-?\\d+.\\d+)+|'\\w+')");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || (!"solaire".equals(url.getScheme()) && !"solaire".equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        BroadcastingHttpResponse response = new BroadcastingHttpResponse();
        if(!url.getHost().equals("broadcast")){
            throw new RuntimeException("unsupported solaire host.");
        }
        setAlreadyRouted(exchange);
        return exchange
                .getRequest()
                .getBody()
                //do broadcast.
                .flatMap(message-> {
                    String json = message.toString(StandardCharsets.UTF_8);
                    Map<String, Object> jsonMap = JsonParserFactory.getJsonParser().parseMap(json);
                    String idEl = extractIdEl(jsonMap.getOrDefault("predicate", "").toString());
                    if(idEl != null){
                        String val = idEl.split("==")[1];
                        if(val.contains("'"))
                            val = val.replace("'", "");
                        return broadcastDistributor.distributeSpecified(json, val);
                    } else {
                        return broadcastDistributor.distributeAllNode(json);
                    }
                })
                .doOnNext(response::add)
                .last(new NodeBroadcastingResponse())
                .map(x->response)
                .doOnError(e-> log.error("unexpected broadcast failed.", e))
                .onErrorResume(e-> Mono.just(BroadcastingHttpResponse.failure("unexpected failed")))
                .flatMap(res->{
                    return BodyInserters
                            .fromValue(res)
                            .insert(exchange.getResponse(), new BodyInserterContext());
                })
                .then(chain.filter(exchange));
    }

    private String extractIdEl(@Nullable String predicate){
        if(predicate == null || predicate.isEmpty()) return null;
        if(predicate.contains("id")){
            Matcher matcher = pattern.matcher(predicate);
            return matcher.find()?matcher.group():null;
        }
        return null;
    }
}
