package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.cluster.DistributeResult;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Ou
 */
public interface Broadcaster {

    enum BroadcastChannel {
        WEBSOCKET("webSocketBroadcaster");

        @Getter
        private String serviceName;

        BroadcastChannel(String serviceName) {
            this.serviceName = serviceName;
        }
    }

    Flux<DistributeResult> broadcast(BroadcastParam broadcastParam);

    Mono<DistributeResult> localBroadcast(BroadcastParam broadcastParam);

    Mono<DistributeResult> localBroadcast(LinkedList<String[]> exprList, BroadcastParam broadcastParam);
}
