package cn.solairelight;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Joel Ou
 */
@Getter
@ToString
public class MessageWrapper<T> {
    @Setter
    private Object message;

    private final T rawMessage;

    @Setter
    private boolean forwardable;

    @Setter
    private Object features;

    private MessageWrapper(T rawMessage){
        this.rawMessage = rawMessage;
    }

    public static <T> MessageWrapper<T> create(T rawMessage){
        return new MessageWrapper<>(rawMessage);
    }
}
