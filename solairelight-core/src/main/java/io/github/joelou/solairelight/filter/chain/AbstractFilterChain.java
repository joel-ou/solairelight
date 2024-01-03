package io.github.joelou.solairelight.filter.chain;

import lombok.extern.slf4j.Slf4j;
import io.github.joelou.solairelight.filter.SolairelightFilter;
import io.github.joelou.solairelight.filter.FilterContext;

import java.util.Set;

/**
 * @author Joel Ou
 */
@Slf4j
public abstract class AbstractFilterChain implements FilterChain {
    private final Set<? extends SolairelightFilter<?>> filters;

    protected AbstractFilterChain(Set<? extends SolairelightFilter<?>> filters) {
        this.filters = sorting(filters);
    }

    @Override
    public FilterContext filter(FilterContext<Object> filterContext) {
        FilterContext relay = filterContext;
        for (SolairelightFilter<?> filter : filters) {
            relay = filter.doFilter(relay);
            if(!relay.isPass()) {
                log.warn("filter chain has abort at {}", filter.getClass().getName());
                return relay.setAbortPoint(filter.getClass());
            }
        }
        return relay;
    }
}
