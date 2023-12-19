package cn.muskmelon.filter.chain;

import cn.muskmelon.filter.message.MessageFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class InboundMessageFilterChain extends AbstractFilterChain {

    public InboundMessageFilterChain(Set<MessageFilter> filters) {
        super(filters);
    }
}
