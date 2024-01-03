package io.github.joelou.solairelight.exception;

import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public class ResponseMessageException extends RuntimeException {
    private final String code;

    public ResponseMessageException(String message) {
        super(message);
        this.code = "e99";
    }

    public ResponseMessageException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ResponseMessageException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
    }

    public ResponseMessageException(ExceptionEnum exceptionEnum, String joint) {
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
    }
}
