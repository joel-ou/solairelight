package cn.solairelight.filter.message;

import cn.solairelight.MessageWrapper;
import cn.solairelight.filter.SolairelightFilter;
import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public abstract class MessageFilter implements SolairelightFilter<MessageWrapper> {

    public enum MessageWay {
        none,
        incoming,
        outgoing;
    }

    protected MessageWay messageWay = MessageWay.none;

    public MessageFilter(MessageWay messageWay) {
        this.messageWay = messageWay;
    }
}
