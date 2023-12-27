package cn.solairelight.filter.chain;

import cn.solairelight.filter.Filter;
import cn.solairelight.filter.FilterContext;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
public interface FilterChain {

    FilterContext<Object> execute(FilterContext<Object> filterContext);

    default Set<? extends Filter<?>> sorting(Set<? extends Filter<?>> filters) {
        return filters.stream().sorted(Comparator.comparingInt(Filter::order)).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
