package cn.solairelight.filter.chain;

import cn.solairelight.filter.session.SessionFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class OutboundMessageFilterChain extends AbstractFilterChain {

    public OutboundMessageFilterChain(Set<SessionFilter> filters) {
        super(filters);
    }
}
