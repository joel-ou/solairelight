package com.github.joelou.solairelight.properties;

import com.github.joelou.solairelight.expression.Operator;
import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class RouteProperties {
    //http://localhost:8081/example
    private String uri;

    private Predicate predicate;

    @Data
    public static class Predicate {
        private String message;
        private String sessionHeader;
        private Operator operator;
    }
}
