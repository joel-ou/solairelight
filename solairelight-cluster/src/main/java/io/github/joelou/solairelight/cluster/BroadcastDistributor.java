package io.github.joelou.solairelight.cluster;

import reactor.core.publisher.Flux;

/**
 * @author Joel Ou
 */
public interface BroadcastDistributor {

    Flux<NodeBroadcastingResponse> distributeSpecified(Object broadcastParam, String id);

    Flux<NodeBroadcastingResponse> distributeAllNode(Object broadcastParam);
}
