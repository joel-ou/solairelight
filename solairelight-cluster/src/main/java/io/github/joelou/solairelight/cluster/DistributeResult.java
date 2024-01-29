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
public class DistributeResult {
    private String nodeId;

    private boolean success;

    private String code;

    private String message;

    public static DistributeResult failure(NodeData.BasicInfo basicInfo, ClusterExceptionEnum exceptionEnum){
        return new DistributeResult(basicInfo.getNodeId(),
                false, exceptionEnum.getCode(), exceptionEnum.getMessage());
    }

    public static DistributeResult failure(NodeData.BasicInfo basicInfo, String error){
        return new DistributeResult(basicInfo.getNodeId(),
                false, "e99",error);
    }

    public static DistributeResult failure(NodeData.BasicInfo basicInfo, String code, String error){
        return new DistributeResult(basicInfo.getNodeId(),
                false, code, error);
    }

    public static DistributeResult success(NodeData.BasicInfo basicInfo){
        return new DistributeResult(basicInfo.getNodeId(),
                true, "0","success.");
    }

    public static Object failure(String code, String message) {
        return new DistributeResult(NodeData.instance.getBasicInfo().getNodeId(),
                false, code, message);
    }
}
