package cn.muskmelon.filter.session;

import cn.muskmelon.session.BasicSession;
import cn.muskmelon.filter.FilterCargo;

/**
 * @author Joel Ou
 */
public class SessionSecurityFilter implements SessionFilter {

    @Override
    public FilterCargo<BasicSession> execute(FilterCargo<?> filterCargo) {
        return FilterCargo.pass();
    }

    @Override
    public int order() {
        return -98;
    }
}
