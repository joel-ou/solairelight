package cn.solairelight.filter;

/**
 * @author Joel Ou
 */
public interface Filter<P> {

    FilterContext<P> execute(FilterContext<P> filterContext);

    default int order() {
        return 0;
    }
}
