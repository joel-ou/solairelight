package cn.muskmelon.expression;

/**
 * @author Joel Ou
 */
public interface ExpressionEvaluator<T> {

    boolean evaluate(String expr, T root);
}
