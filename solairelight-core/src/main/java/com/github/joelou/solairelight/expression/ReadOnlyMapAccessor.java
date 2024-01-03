package com.github.joelou.solairelight.expression;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import java.util.Map;
import java.util.UUID;

/**
 * @author Joel Ou
 */
public class ReadOnlyMapAccessor extends MapAccessor {
    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof Map<?, ?>;
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) {
        return false;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        Map<?, ?> map = (Map<?, ?>) target;
        Object value = map.get(name);
        if(value == null){
            //avoid null==null is true problem
            return new TypedValue(UUID.randomUUID().toString());
        }
        return new TypedValue(value);
    }
}
