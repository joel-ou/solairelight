package io.github.joelou.solairelight.exception;

/**
 * @author Joel Ou
 */
public class NoSessionFoundException extends ResponseMessageException{

    public NoSessionFoundException(){
        super(ExceptionEnum.NO_SESSION_FOUND);
    }
}
