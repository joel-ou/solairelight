package cn.solairelight.filter.session;

import cn.solairelight.session.BasicSession;
import cn.solairelight.filter.FilterCargo;

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
