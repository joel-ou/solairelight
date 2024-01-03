package com.github.joelou.solairelight.demo.filter;

import com.github.joelou.solairelight.filter.FilterContext;
import com.github.joelou.solairelight.filter.session.SessionFilter;
import com.github.joelou.solairelight.session.BasicSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class SessionFilterImpl implements SessionFilter {
    @Override
    public FilterContext<BasicSession> doFilter(FilterContext<BasicSession> filterContext) {
        System.out.println(filterContext.getPayload().getSessionId());
        log.info("session filter. {}", filterContext);
        return FilterContext.pass(filterContext.getPayload());
    }
}
