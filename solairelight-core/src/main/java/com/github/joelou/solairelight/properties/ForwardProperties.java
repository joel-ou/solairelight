package com.github.joelou.solairelight.properties;

import lombok.Data;
import lombok.ToString;

/**
 * @author Joel Ou
 */
@Data
@ToString
public class ForwardProperties {

    private boolean enable = true;

    private String forwardHeader = "all";

    private RouteProperties[] routes;
}
