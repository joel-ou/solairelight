package io.github.joelou.solairelight.gateway;

import io.github.joelou.solairelight.cluster.NodeBroadcastingResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Ou
 */
@Data
public class BroadcastingHttpResponse {
    private String message;
    private boolean cluster = true;
    private List<NodeBroadcastingResponse> clusterResult = new ArrayList<>(10);

    public static BroadcastingHttpResponse failure(String message){
        BroadcastingHttpResponse response = new BroadcastingHttpResponse();
        response.setMessage(message);
        return response;
    }

    public void add(NodeBroadcastingResponse result){
        this.clusterResult.add(result);
    }
}
