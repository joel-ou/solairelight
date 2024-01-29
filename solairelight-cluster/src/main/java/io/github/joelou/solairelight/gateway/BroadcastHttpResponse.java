package io.github.joelou.solairelight.gateway;

import io.github.joelou.solairelight.cluster.DistributeResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Ou
 */
@Data
public class BroadcastHttpResponse {
    private String message;
    private boolean cluster = true;
    private List<DistributeResult> clusterResult = new ArrayList<>(10);

    public static BroadcastHttpResponse failure(String message){
        BroadcastHttpResponse response = new BroadcastHttpResponse();
        response.setMessage(message);
        return response;
    }

    public void add(DistributeResult result){
        this.clusterResult.add(result);
    }
}
