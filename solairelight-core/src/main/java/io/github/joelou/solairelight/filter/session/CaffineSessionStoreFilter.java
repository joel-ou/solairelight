package io.github.joelou.solairelight.filter.session;

import io.github.joelou.solairelight.filter.FilterContext;
import io.github.joelou.solairelight.session.BasicSession;
import io.github.joelou.solairelight.session.SessionBroker;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Component
public class CaffineSessionStoreFilter implements SessionFilter {

    @Override
    public FilterContext<BasicSession> doFilter(FilterContext<BasicSession> filterContext) {
        BasicSession basicSession = filterContext.getPayload();
        //storage the session
        SessionBroker.getStorage().put(basicSession.getSessionId(), basicSession);
        return FilterContext.pass(basicSession);
    }

    @Override
    public int order() {
        return -97;
    }
}
