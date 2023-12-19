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
public class FilterCargo<P> {

    private boolean pass = true;

    private P payload;

    public static <P> FilterCargo<P> init(P payload){
        return new FilterCargo<>(true, payload);
    }

    public static <P> FilterCargo<P> pass(){
        return new FilterCargo<>();
    }

    public static <P> FilterCargo<P> pass(P payload){
        return new FilterCargo<>(true, payload);
    }

    public static <P> FilterCargo<P> abort(){
        return new FilterCargo<>(false, null);
    }
}
