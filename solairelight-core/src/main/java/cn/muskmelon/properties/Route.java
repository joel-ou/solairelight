package cn.muskmelon.properties;

import cn.muskmelon.expression.Operator;
import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class Route {
    //http://localhost:8081/example
    private String uri;

    private Predicate predicate;

    @Data
    public static class Predicate {
        private String message;
        private String header;
        private Operator operator;
    }
}
