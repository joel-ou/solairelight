package cn.solairelight.properties;

import lombok.Data;
import lombok.ToString;

/**
 * @author Joel Ou
 */
@Data
@ToString
public class SolairelightZookeeperProperties {
    private String hostPort;
    private String authInfo;
}
