package cn.solairelight.brodcast;

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

    private String message;

    private int retryTimes;
}
