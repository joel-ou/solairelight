package io.github.joelou.solairelight.properties;

import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class WebsocketProperties {
    private String path = "solairelight";
    private String domain = null;
}
