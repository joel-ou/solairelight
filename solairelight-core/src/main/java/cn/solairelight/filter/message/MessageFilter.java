package cn.solairelight.filter.message;

import cn.solairelight.MessageWrapper;
import cn.solairelight.filter.Filter;
import lombok.Getter;

/**
 * @author Joel Ou
 */
@Getter
public abstract class MessageFilter implements Filter<MessageWrapper<Object>> {

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
