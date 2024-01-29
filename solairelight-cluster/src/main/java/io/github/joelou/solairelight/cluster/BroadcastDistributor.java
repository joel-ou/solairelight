package io.github.joelou.solairelight.cluster;

import reactor.core.publisher.Flux;

/**
 * @author Joel Ou
 */
public interface BroadcastDistributor {

    Flux<DistributeResult> distributeSpecified(Object broadcastParam, NodeData.BasicInfo basicInfo);

    Flux<DistributeResult> distributeAllNode(Object broadcastParam);
}
