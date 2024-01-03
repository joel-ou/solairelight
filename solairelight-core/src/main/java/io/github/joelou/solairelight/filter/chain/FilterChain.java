package io.github.joelou.solairelight.filter.chain;

import io.github.joelou.solairelight.filter.SolairelightFilter;
import io.github.joelou.solairelight.filter.FilterContext;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
public interface FilterChain {

    FilterContext<Object> filter(FilterContext<Object> filterContext);

    default Set<? extends SolairelightFilter<?>> sorting(Set<? extends SolairelightFilter<?>> filters) {
        return filters.stream().sorted(Comparator.comparingInt(SolairelightFilter::order)).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
