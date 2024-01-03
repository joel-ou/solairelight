package io.github.joelou.solairelight.exception;

import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public class DuplicatedBroadcastException extends ResponseMessageException {
    private final String code;

    public DuplicatedBroadcastException() {
        super(ExceptionEnum.DUPLICATED_BROADCAST.getMessage());
        this.code = ExceptionEnum.DUPLICATED_BROADCAST.getCode();
    }
}
