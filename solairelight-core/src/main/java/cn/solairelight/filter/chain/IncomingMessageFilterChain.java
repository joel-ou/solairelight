package cn.solairelight.filter.chain;

import cn.solairelight.filter.message.MessageFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class IncomingMessageFilterChain extends AbstractFilterChain {

    public IncomingMessageFilterChain(Set<MessageFilter> filters) {
        super(filters);
    }
}
