package cn.solairelight.filter.chain;

import cn.solairelight.filter.session.SessionFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class SessionFilterChain extends AbstractFilterChain {

    public SessionFilterChain(Set<SessionFilter> filters) {
        super(filters);
    }
}
