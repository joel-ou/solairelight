package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.SolairelightSettings;
import io.github.joelou.solairelight.cluster.DistributeResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Ou
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BroadcastHttpResponse {
    private String message;
    private boolean cluster = SolairelightSettings.isCluster();
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
