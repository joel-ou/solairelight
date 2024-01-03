package io.github.joelou.solairelight.cluster;

import io.github.joelou.solairelight.brodcast.BroadcastParam;
import reactor.core.publisher.Flux;

/**
 * @author Joel Ou
 */
public interface BroadcastDistributor {

    Flux<DistributeResult> distributeSpecified(BroadcastParam broadcastParam, NodeData.BasicInfo basicInfo);

    Flux<DistributeResult> distributeAllNode(BroadcastParam broadcastParam);
}
