package com.github.joelou.solairelight;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Joel Ou
 */
@Getter
@ToString
public class MessageWrapper {
    @Setter
    private Object message;

    private final Object rawMessage;

    @Setter
    private boolean forwardable;

    @Setter
    private Object features;

    private MessageWrapper(Object rawMessage){
        this.rawMessage = rawMessage;
    }

    public static MessageWrapper create(Object rawMessage){
        return new MessageWrapper(rawMessage);
    }

    public Object getMessage(){
        return message==null?rawMessage:message;
    }
}
