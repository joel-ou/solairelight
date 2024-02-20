package io.github.joelou.solairelight.actuator;

import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Joel Ou
 */
@Component
@Endpoint(id="nodeOverview")
public class SolairelightActuatorEndpoints {

    @ReadOperation
    public List<NodeData> overview(){
        return SolairelightRedisClient.getInstance().getNodeCache();
    }
}
