package cn.solairelight.filter.chain;

import cn.solairelight.filter.SolairelightFilter;
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

    default Set<? extends SolairelightFilter<?>> sorting(Set<? extends SolairelightFilter<?>> filters) {
        return filters.stream().sorted(Comparator.comparingInt(SolairelightFilter::order)).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
