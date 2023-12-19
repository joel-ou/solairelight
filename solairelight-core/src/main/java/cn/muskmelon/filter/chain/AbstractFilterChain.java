package cn.muskmelon.filter.chain;

import lombok.extern.slf4j.Slf4j;
import cn.muskmelon.filter.Filter;
import cn.muskmelon.filter.FilterCargo;

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
    public FilterCargo<?> execute(FilterCargo<?> filterCargo) {
        FilterCargo<?> relay = filterCargo;
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
