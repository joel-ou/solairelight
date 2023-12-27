package cn.solairelight.filter.chain;

import cn.solairelight.filter.message.MessageFilter;
import cn.solairelight.filter.session.SessionFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class OutgoingMessageFilterChain extends AbstractFilterChain {

    public OutgoingMessageFilterChain(Set<MessageFilter> filters) {
        super(filters);
    }
}
