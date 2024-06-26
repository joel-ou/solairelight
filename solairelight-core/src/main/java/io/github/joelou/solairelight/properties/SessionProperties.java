package io.github.joelou.solairelight.properties;

import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class SessionProperties {

    private int idle = 600;

    private int maxNumber = 0;
}
