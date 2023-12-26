package cn.solairelight.properties;

import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class ClusterProperties {
    private boolean enable;

    private String nodeIdSuffix;

    public String getNodeIdSuffix() {
        String env = System.getenv("NODE_ID_SUFFIX");
        if(env != null && !env.isEmpty()) {
            return env;
        }
        return nodeIdSuffix;
    }
}
