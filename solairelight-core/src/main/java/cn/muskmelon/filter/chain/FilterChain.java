package cn.muskmelon.filter.chain;

import cn.muskmelon.filter.Filter;
import cn.muskmelon.filter.FilterCargo;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
public interface FilterChain {

    FilterCargo<?> execute(FilterCargo<?> filterCargo);

    default Set<Filter<?>> sorting(Set<? extends Filter<?>> filters) {
        return filters.stream().sorted(Comparator.comparingInt((Filter<?> v) -> v.order())).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
