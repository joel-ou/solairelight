package cn.solairelight.filter;

/**
 * @author Joel Ou
 */
public interface Filter<P> {

    FilterCargo<P> execute(FilterCargo<?> filterCargo);

    default int order() {
        return 0;
    }
}
