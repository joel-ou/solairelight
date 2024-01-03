package com.github.joelou.solairelight.filter.message;

import com.github.joelou.solairelight.MessageWrapper;
import com.github.joelou.solairelight.filter.SolairelightFilter;
import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public abstract class MessageFilter implements SolairelightFilter<MessageWrapper> {

    public enum MessageWay {
        none,
        incoming,
        broadcast;
    }

    protected MessageWay messageWay = MessageWay.none;

    public MessageFilter(MessageWay messageWay) {
        this.messageWay = messageWay;
    }
}
