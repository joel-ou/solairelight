package com.github.joelou.solairelight.expression;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.function.BiPredicate;

/**
 * @author Joel Ou
 */
@Slf4j
public class SpringExpressionEvaluator<T> implements ExpressionEvaluator<T> {
    private static final StandardEvaluationContext context = new StandardEvaluationContext();

    static {
        context.addPropertyAccessor(new ReadOnlyMapAccessor());
    }

    private static final BiPredicate<String, Object> commonPredicate = (expr, root)->{
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression expression = expressionParser.parseExpression(expr);
        Boolean result = expression.getValue(SpringExpressionEvaluator.context,
                root,
                Boolean.class);
        return Boolean.TRUE.equals(result);
    };

    @Override
    public boolean evaluate(String expr, T root) {
        return commonPredicate.test(expr, root);
    }
}
