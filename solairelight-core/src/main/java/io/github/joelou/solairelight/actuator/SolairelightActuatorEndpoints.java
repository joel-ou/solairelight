package io.github.joelou.solairelight.actuator;

import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Ou
 */
@Component
@Endpoint(id="nodeOverview")
public class SolairelightActuatorEndpoints {

    @ReadOperation
    public LinkedList<NodeData> overview(){
        List<NodeData> nodeData = SolairelightRedisClient.getInstance().getNodeCache();
        LinkedList<NodeData> nodeDataLinked = new LinkedList<>(nodeData);
        nodeDataLinked.addFirst(NodeData.instance);
        return nodeDataLinked;
    }
}
