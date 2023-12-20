package cn.solairelight.filter.chain;

import lombok.extern.slf4j.Slf4j;
import cn.solairelight.filter.Filter;
import cn.solairelight.filter.FilterContext;

import java.util.Set;

/**
 * @author Joel Ou
 */
@Slf4j
public abstract class AbstractFilterChain implements FilterChain {
    private final Set<? extends Filter<?>> filters;

    protected AbstractFilterChain(Set<? extends Filter<?>> filters) {
        this.filters = sorting(filters);
    }

    @Override
    public FilterContext<?> execute(FilterContext<?> filterContext) {
        FilterContext<?> relay = filterContext;
        for (Filter<?> filter : filters) {
            relay = filter.execute(relay);
            if(!relay.isPass()) {
                log.warn("filter chain has abort at {}", filter.getClass().getName());
                return relay;
            }
        }
        return relay;
    }
}
