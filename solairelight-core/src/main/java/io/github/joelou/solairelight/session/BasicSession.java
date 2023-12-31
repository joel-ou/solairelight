package io.github.joelou.solairelight.session;

import io.github.joelou.solairelight.cluster.ClusterTools;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.reactive.socket.HandshakeInfo;

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

    public abstract HandshakeInfo getHandshakeInfo();
}
