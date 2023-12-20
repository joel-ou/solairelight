package cn.solairelight.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Data
@Component
@ConfigurationProperties(prefix = "solairelight")
@ToString
public class SolairelightProperties {
    private boolean enable = true;
    private String webSocketPath = "/path";
    private SecureProperties secure;
    private MessageProperties message;
    private SessionProperties session;
    private ForwardProperties forward;
    private String zookeeper;
}
