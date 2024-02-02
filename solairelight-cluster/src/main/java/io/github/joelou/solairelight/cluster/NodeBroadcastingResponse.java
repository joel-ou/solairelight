package io.github.joelou.solairelight.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joel Ou
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeBroadcastingResponse {
    private String nodeId;

    private boolean success;

    private String code;

    private String message;

    public static NodeBroadcastingResponse failure(NodeData.BasicInfo basicInfo, ClusterExceptionEnum exceptionEnum){
        return new NodeBroadcastingResponse(basicInfo.getNodeId(),
                false, exceptionEnum.getCode(), exceptionEnum.getMessage());
    }

    public static NodeBroadcastingResponse failure(NodeData.BasicInfo basicInfo, String error){
        return new NodeBroadcastingResponse(basicInfo.getNodeId(),
                false, "e99",error);
    }

    public static NodeBroadcastingResponse failure(NodeData.BasicInfo basicInfo, String code, String error){
        return new NodeBroadcastingResponse(basicInfo.getNodeId(),
                false, code, error);
    }

    public static NodeBroadcastingResponse success(NodeData.BasicInfo basicInfo){
        return new NodeBroadcastingResponse(basicInfo.getNodeId(),
                true, "0","success");
    }

    public static NodeBroadcastingResponse failure(String code, String message) {
        return new NodeBroadcastingResponse(NodeData.instance.getBasicInfo().getNodeId(),
                false, code, message);
    }
}
