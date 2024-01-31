package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.cluster.NodeBroadcastingResponse;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

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

    Mono<NodeBroadcastingResponse> localBroadcast(BroadcastParam broadcastParam);

    Mono<NodeBroadcastingResponse> localBroadcast(LinkedList<String[]> exprList, BroadcastParam broadcastParam);
}
