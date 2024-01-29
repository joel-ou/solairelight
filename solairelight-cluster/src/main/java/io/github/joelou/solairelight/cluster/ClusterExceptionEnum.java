package io.github.joelou.solairelight.cluster;

import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public enum ClusterExceptionEnum {
    DISTRIBUTE_FAILED_HTTP("e101", "distribute failed http error.");

    private final String code;
    private final String message;

    ClusterExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
