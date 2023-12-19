package cn.solairelight.expression;

/**
 * @author Joel Ou
 */
public interface ExpressionEvaluator<T> {

    boolean evaluate(String expr, T root);
}
