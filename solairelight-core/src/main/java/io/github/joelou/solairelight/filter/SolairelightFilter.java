package io.github.joelou.solairelight.filter;

/**
 * @author Joel Ou
 */
public interface SolairelightFilter<P> {

    FilterContext<P> doFilter(FilterContext<P> filterContext);

    default int order() {
        return 99;
    }
}
