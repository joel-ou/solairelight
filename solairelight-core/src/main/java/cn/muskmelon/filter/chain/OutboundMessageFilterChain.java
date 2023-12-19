package cn.muskmelon.filter.chain;

import cn.muskmelon.filter.session.SessionFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class OutboundMessageFilterChain extends AbstractFilterChain {

    public OutboundMessageFilterChain(Set<SessionFilter> filters) {
        super(filters);
    }
}
