package cn.muskmelon.exception;

/**
 * @author Joel Ou
 */
public class NoSessionFoundException extends ResponseMessageException{

    public NoSessionFoundException(){
        super(ExceptionEnum.NO_SESSION_FOUND);
    }
}
