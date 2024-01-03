package io.github.joelou.solairelight.exception;

/**
 * @author Joel Ou
 */
public class UnsupportedBroadcastingMessageException extends ResponseMessageException{

    public UnsupportedBroadcastingMessageException(){
        super(ExceptionEnum.UNSUPPORTED_BROADCASTING_MESSAGE);
    }
}
