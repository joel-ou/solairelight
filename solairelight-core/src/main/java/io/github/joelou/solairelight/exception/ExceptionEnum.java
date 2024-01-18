package io.github.joelou.solairelight.exception;

import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public enum ExceptionEnum {
    NO_SESSION_FOUND("e01", "no session found"),
    INVALID_RANGE_VALUE("e02", "invalid range value: "),
    OPERATION_NOT_SUPPORTED("e03", "setValue operation not supported."),
    DUPLICATED_BROADCAST("e04", "duplicated broadcast."),
    UNSUPPORTED_BROADCASTING_MESSAGE("e05", "unsupported broadcasting message only support String & Bytes"),
    FILTER_ABORTED("e06", "filter aborted."),
    DISTRIBUTE_FAILED_HTTP("e07", "distribute failed http error."),
    INVALID_PREDICATE_VALUE("e08", "invalid predicate argument.");

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
