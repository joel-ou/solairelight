package cn.muskmelon.properties;

import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class SessionProperties {
    private int idle = 600;
    private int maxNumber = 20000;
}
