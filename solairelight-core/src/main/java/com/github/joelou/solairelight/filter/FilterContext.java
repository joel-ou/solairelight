package com.github.joelou.solairelight.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joel Ou
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterContext<P> {

    private boolean pass = true;

    private P payload;

    private Class<?> abortPoint;

    public FilterContext<?> setAbortPoint(Class<?> abortPoint) {
        this.abortPoint = abortPoint;
        return this;
    }

    public static <P> FilterContext<P> init(P payload){
        return new FilterContext<>(true, payload, null);
    }

    public static <P> FilterContext<P> pass(P payload){
        return new FilterContext<>(true, payload, null);
    }

    public static <P> FilterContext<P> pass(FilterContext<P> preceding){
        return new FilterContext<>(true, preceding.getPayload(), null);
    }

    public static <P> FilterContext<P> abort(){
        return new FilterContext<>(false, null, null);
    }
}
