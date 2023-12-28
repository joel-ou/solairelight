package cn.solairelight.runner.demo.filter;

import cn.solairelight.filter.FilterContext;
import cn.solairelight.filter.session.SessionFilter;
import cn.solairelight.session.BasicSession;
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