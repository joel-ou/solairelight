package io.github.joelou.solairelight.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Ou
 */
@Data
@ConfigurationProperties(prefix = "solairelight")
@ToString
public class SolairelightProperties {
    private boolean enable = true;
    private WebsocketProperties websocket;
    private SecureProperties secure;
    private MessageProperties message;
    private SessionProperties session;
    private ForwardProperties forward;
    private ClusterProperties cluster;
}
