package cn.solairelight.exception;

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
    UNSUPPORTED_BROADCASTING_MESSAGE("e05", "unsupported broadcasting message only support String & Bytes");

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
