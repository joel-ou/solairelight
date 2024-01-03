package io.github.joelou.solairelight.brodcast;

import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class BroadcastParam {

    private String id;

    private String channel;

    private String range;

    private String predicate;

    private Object message;

    private int retryTimes;
}
