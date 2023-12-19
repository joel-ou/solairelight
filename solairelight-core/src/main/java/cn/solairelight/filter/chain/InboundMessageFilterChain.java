package cn.solairelight.filter.chain;

import cn.solairelight.filter.message.MessageFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class InboundMessageFilterChain extends AbstractFilterChain {

    public InboundMessageFilterChain(Set<MessageFilter> filters) {
        super(filters);
    }
}
