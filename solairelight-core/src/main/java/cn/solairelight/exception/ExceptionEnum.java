package cn.solairelight.exception;

import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public enum ExceptionEnum {
    NO_SESSION_FOUND("e01", "no session found"),
    INVALID_RANGE_VALUE("e02", "invalid range value: "),
    OPERATION_NOT_SUPPORTED("e03", "setValue operation not supported.");

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
