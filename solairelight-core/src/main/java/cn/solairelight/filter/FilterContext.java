package cn.solairelight.filter;

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

    public static <P> FilterContext<P> init(P payload){
        return new FilterContext<>(true, payload);
    }

    public static <P> FilterContext<P> pass(){
        return new FilterContext<>();
    }

    public static <P> FilterContext<P> pass(P payload){
        return new FilterContext<>(true, payload);
    }

    public static <P> FilterContext<P> abort(){
        return new FilterContext<>(false, null);
    }
}
