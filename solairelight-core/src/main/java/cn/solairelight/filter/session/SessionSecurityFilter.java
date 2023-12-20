package cn.solairelight.filter.session;

import cn.solairelight.session.BasicSession;
import cn.solairelight.filter.FilterContext;

/**
 * @author Joel Ou
 */
public class SessionSecurityFilter implements SessionFilter {

    @Override
    public FilterContext<BasicSession> execute(FilterContext<?> filterContext) {
        return FilterContext.pass();
    }

    @Override
    public int order() {
        return -98;
    }
}
