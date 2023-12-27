package cn.solairelight.session;

import cn.solairelight.cluster.ClusterTools;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Ou
 */
@ToString
public abstract class BasicSession {

    @Getter
    @Setter
    private boolean closed = false;

    private final String serviceId = ClusterTools.getNodeId();

    private String sessionId;

    @Setter
    @Getter
    private String clientIP;

    @Getter
    private final Map<String, String> sessionHeads = new HashMap<>();

    @Setter
    @Getter
    private UserMetadata userMetadata = new UserMetadata();

    public String getSessionId() {
        return this.serviceId+":"+sessionId;
    }

    protected void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public abstract void close();
}
