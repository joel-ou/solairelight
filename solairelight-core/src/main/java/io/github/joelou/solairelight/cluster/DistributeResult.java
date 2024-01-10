package io.github.joelou.solairelight.cluster;

import io.github.joelou.solairelight.exception.ExceptionEnum;
import io.github.joelou.solairelight.exception.ResponseMessageException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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

    public static DistributeResult failure(NodeData.BasicInfo basicInfo, ExceptionEnum exceptionEnum){
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

    public static DistributeResult failure(NodeData.BasicInfo basicInfo, ResponseMessageException messageException){
        return new DistributeResult(basicInfo.getNodeId(),
                false, messageException.getCode(), messageException.getMessage());
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
