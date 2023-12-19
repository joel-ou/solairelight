package cn.muskmelon.session;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Ou
 */
public abstract class BasicSession {

    @Getter
    @Setter
    private boolean closed = false;

    @Setter
    private String serviceId;

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
