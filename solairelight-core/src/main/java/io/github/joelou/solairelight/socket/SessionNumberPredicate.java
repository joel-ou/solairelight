package io.github.joelou.solairelight.socket;

import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

/**
 * @author Joel Ou
 */
@Deprecated
public class SessionNumberPredicate implements BiPredicate<Object, ServerWebExchange> {

    @Override
    public boolean test(Object object, ServerWebExchange serverWebExchange) {
        AtomicInteger sessionNumber = NodeData.instance.getSessionQuota();
        if(sessionNumber.get() <= 0){
            return false;
        }
        //try to acquire a session number.
        return sessionNumber.getAndAccumulate(0, (prev, x) -> prev > 0 ? prev - 1 : prev) > 0;
    }

    private boolean redirect(ServerWebExchange serverWebExchange){
        SolairelightRedisClient.getInstance().getNodeCache()
                .stream()
                .max(Comparator.comparingInt(obj -> obj.getSessionQuota().get()))
                .ifPresent(nodeData -> {
                    if(nodeData.getSessionQuota().get() <= 0)return;
                    String uri = nodeData.getBasicInfo().getUrl();
                    uri += serverWebExchange.getRequest().getPath();
                    ServerHttpResponse response = serverWebExchange.getResponse();
                    response.setStatusCode(HttpStatus.FOUND);
                    response.getHeaders().add("Location", uri);
                });
        return false;
    }
}
