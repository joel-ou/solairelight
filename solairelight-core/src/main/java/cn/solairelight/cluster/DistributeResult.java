package cn.solairelight.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author Joel Ou
 */
@Data
@AllArgsConstructor
public class DistributeResult {
    private String nodeId;

    private HttpStatus status;
}
