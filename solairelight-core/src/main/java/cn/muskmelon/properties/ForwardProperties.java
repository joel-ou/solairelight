package cn.muskmelon.properties;

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

    private Route[] routes;
}
